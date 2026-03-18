package main

import (
	"bufio"
	"encoding/json"
	"errors"
	"fmt"
	"os"
	"strings"
	"time"

	"golang.org/x/crypto/bcrypt"
)

const accountsFile = "accounts.json"

// Account represents a single user account record.
type Account struct {
	Username        string    `json:"username"`
	HashedPassword  string    `json:"hashed_password"`
	Role            string    `json:"role"`
	CreatedAt       time.Time `json:"created_at"`
}

// Registry holds all accounts in memory.
type Registry struct {
	Accounts []Account `json:"accounts"`
}

// validRoles defines the allowed roles.
var validRoles = map[string]bool{
	"admin":   true,
	"auditor": true,
	"analyst": true,
}

// loadRegistry reads accounts.json and returns a Registry.
// If the file does not exist, it returns an empty Registry.
func loadRegistry() (*Registry, error) {
	data, err := os.ReadFile(accountsFile)
	if err != nil {
		if errors.Is(err, os.ErrNotExist) {
			return &Registry{Accounts: []Account{}}, nil
		}
		return nil, fmt.Errorf("error reading %s: %w", accountsFile, err)
	}

	// Handle empty file gracefully.
	if len(strings.TrimSpace(string(data))) == 0 {
		return &Registry{Accounts: []Account{}}, nil
	}

	var reg Registry
	if err := json.Unmarshal(data, &reg); err != nil {
		return nil, fmt.Errorf("accounts file is corrupted or has an invalid structure: %w", err)
	}

	// Validate that every record has required fields.
	for i, acc := range reg.Accounts {
		if acc.Username == "" || acc.HashedPassword == "" || acc.Role == "" {
			return nil, fmt.Errorf("invalid account record at index %d: missing required fields", i)
		}
	}

	return &reg, nil
}

// saveRegistry persists the registry to accounts.json.
func saveRegistry(reg *Registry) error {
	data, err := json.MarshalIndent(reg, "", "  ")
	if err != nil {
		return fmt.Errorf("failed to serialize accounts: %w", err)
	}
	if err := os.WriteFile(accountsFile, data, 0600); err != nil {
		return fmt.Errorf("failed to write %s: %w", accountsFile, err)
	}
	return nil
}

// findAccount returns a pointer to the account with the given username, or nil.
func findAccount(reg *Registry, username string) *Account {
	for i := range reg.Accounts {
		if reg.Accounts[i].Username == username {
			return &reg.Accounts[i]
		}
	}
	return nil
}

// register creates a new account after validating inputs.
func register(reg *Registry, reader *bufio.Reader) {
	fmt.Print("Enter username: ")
	username, _ := reader.ReadString('\n')
	username = strings.TrimSpace(username)

	if username == "" {
		fmt.Println("Error: username cannot be empty.")
		return
	}

	if findAccount(reg, username) != nil {
		fmt.Println("Error: username already exists. Please choose a different username.")
		return
	}

	fmt.Print("Enter password: ")
	password, _ := reader.ReadString('\n')
	password = strings.TrimSpace(password)

	if len(password) < 6 {
		fmt.Println("Error: password must be at least 6 characters long.")
		return
	}

	fmt.Print("Enter role (admin/auditor/analyst): ")
	role, _ := reader.ReadString('\n')
	role = strings.TrimSpace(strings.ToLower(role))

	if !validRoles[role] {
		fmt.Println("Error: invalid role. Must be one of: admin, auditor, analyst.")
		return
	}


	hash, err := bcrypt.GenerateFromPassword([]byte(password), 10)
	if err != nil {
		fmt.Printf("Error: failed to secure password: %v\n", err)
		return
	}

	// Overwrite the password variable so it does not linger in memory.
	password = ""

	account := Account{
		Username:       username,
		HashedPassword: string(hash),
		Role:           role,
		CreatedAt:      time.Now().UTC(),
	}

	reg.Accounts = append(reg.Accounts, account)

	if err := saveRegistry(reg); err != nil {
		// Roll back in-memory addition on save failure.
		reg.Accounts = reg.Accounts[:len(reg.Accounts)-1]
		fmt.Printf("Error: could not persist account: %v\n", err)
		return
	}

	fmt.Println("Account successfully created.")
}

// login validates credentials against stored records.
func login(reg *Registry, reader *bufio.Reader) {
	fmt.Print("Enter username: ")
	username, _ := reader.ReadString('\n')
	username = strings.TrimSpace(username)

	fmt.Print("Enter password: ")
	password, _ := reader.ReadString('\n')
	password = strings.TrimSpace(password)

	acc := findAccount(reg, username)
	if acc == nil {
		// Use a constant-time path to avoid username enumeration timing attacks.
		bcrypt.CompareHashAndPassword([]byte("$2a$12$invalidhashpadding000000000000000000000000000000000000"), []byte(password)) //nolint
		password = ""
		fmt.Println("Invalid credentials.")
		return
	}

	
	err := bcrypt.CompareHashAndPassword([]byte(acc.HashedPassword), []byte(password))
	password = "" // clear from memory immediately after comparison

	if err != nil {
		fmt.Println("Invalid credentials.")
		return
	}

	fmt.Printf("Login successful. Welcome, %s! (Role: %s)\n", acc.Username, acc.Role)
}

func printMenu() {
	fmt.Println("\n=== Enterprise Account Registry ===")
	fmt.Println("1. Register")
	fmt.Println("2. Login")
	fmt.Println("3. Exit")
	fmt.Print("Select an option: ")
}

func main() {
	reg, err := loadRegistry()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Startup error: %v\n", err)
		fmt.Fprintln(os.Stderr, "Please fix or remove the corrupted accounts file and restart.")
		os.Exit(1)
	}

	reader := bufio.NewReader(os.Stdin)

	for {
		printMenu()
		choice, _ := reader.ReadString('\n')
		choice = strings.TrimSpace(choice)

		switch choice {
		case "1":
			register(reg, reader)
		case "2":
			login(reg, reader)
		case "3":
			fmt.Println("Goodbye.")
			os.Exit(0)
		default:
			fmt.Println("Invalid option. Please enter 1, 2, or 3.")
		}
	}
}