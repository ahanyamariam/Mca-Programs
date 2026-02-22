
class Beverages{
    String name;
    int quantity;
    int price;
    String exp_date;
    Beverages(){}
    Beverages(String n,int q,int p,String exp){
        name=n;
        quantity=q;
        price=p;
        exp_date=exp;
    }
    void display(){
        System.out.println("Beverage Details:");
        System.out.println("name: "+name);
        System.out.println("quantity: "+quantity);
        System.out.println("price: "+price);
        System.out.println("expiry date: "+exp_date);


    }
}
class Vegetables{
    String name;
    int quantity;
    int price;
    String breed;
    Vegetables(){}
    Vegetables(String n,int q,int p,String b){
        name=n;
        quantity=q;
        price=p;
        breed=b;
    }
    void display(){
        System.out.println("Vegetable Details:");
        System.out.println("name: "+name);
        System.out.println("quantity: "+quantity);
        System.out.println("price: "+price);
        System.out.println("breed: "+breed);




    }
}


class Main{
    public static void main(String args[]){
        Beverages item= new Beverages();
        item.name="Coke Zero";
        item.quantity=60;
        item.price=10;
        item.exp_date="12/09/2025";
        item.display();


        Beverages item2=new Beverages("Orange Fanta",20,10,"18/09/2025");
        item2.display();
        Vegetables veg= new Vegetables();
        veg.name="Potato";
        veg.quantity=60;
        veg.price=10;
        veg.breed="Sweet Potato";
        veg.display();


        Vegetables veg2=new Vegetables("Onion",20,10,"Red Onion");
        veg2.display();
    }
}



