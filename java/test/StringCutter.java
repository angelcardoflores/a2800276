 import java.util.*; //(keine ahunung noch was sonst)
 
 public class StringCutter
  {
 
    public StringCutter(String string, int width){
 
          cutString(string, width);
 
    }
 
  
    public static void main(String[] args){
      
      StringCutter sCutter = new StringCutter(args[0], Integer.parseInt(args[1])); // tsk tsk
    }
 
    public void cutString(String string, int width){
         String sString[] = getStringArray(string);
 
       
         for(int i= 0 ; i < sString.length; i++){
               
                while(sString[i].length() < width){
                     if((sString[i] + sString[i+1]).length() > width ){
                         break;
                     }
                     else{
                       sString[i] = sString[i]+sString[i++];
                     }
 
                }
            
            sString[i] = sString[i]+"\n";
            
             System.out.println(sString[i]);
 
         }
 
    }
 
    public String [] getStringArray(String string){
       StringTokenizer st = new StringTokenizer(string);
       int count = st.countTokens();
       int i = 0;        
 
       String sString[] = new String[count];
 
      while (st.hasMoreTokens()) {
          sString[i++] = st.nextToken();
      }
 
      return sString;
    }
 
  }
  
