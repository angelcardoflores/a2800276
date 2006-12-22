class BaseClass{

    public BaseClass(){

        System.out.println("BaseClass Constructor Called");
        doStuff();
    }

    public void doStuff(){
        System.out.println("BaseClass doStuff() Called");
    }
    
    public void doStuffWithMe () {
   	doStuff(); 
    }
    

}

public class DerivedClass extends BaseClass{

    public DerivedClass(){

        System.out.println("DerivedClass Constructor Called");

    }

   public void doStuff(){
        System.out.println("DerivedClass doStuff() Called");
    }

    public static void main(String[] args){

	    DerivedClass dc = new DerivedClass();
	    dc.doStuffWithMe();
    }

}
