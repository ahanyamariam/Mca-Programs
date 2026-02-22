package main
import (
	"fmt"
)
func test2(x func (int)int)int{
	return x(10)

}
func main(){
	test := func(x int)int{
		return x * 2
	}
	fmt.Println(test2(test))
}