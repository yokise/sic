import java.util.*;

import javax.tools.OptionChecker;

import java.io.*;

public class Sic {

    public static Map<String, String> stringMap = new HashMap<>();
    public static Map<String, String> sybMap = new HashMap<>();
    public static String loc = "0000";
    public static String nextloc;

    public static void main(String[] args) {
        readop();
        pass1();
        pass2();
    }

    public static void readop() {
        try {
            Scanner sc = new Scanner(new File("op.txt"));
            String a;
            String b;
            while (sc.hasNext()) {
                a = sc.next();
                b = sc.nextLine();
                stringMap.put(a, b);
            }
            sc.close();
        } catch (FileNotFoundException e) {

            ;
        }
    }

    public static void pass1() {
        String label;
        String opcode;
        String operand;
        String input;
        try {
            Scanner sc = new Scanner(new File("Figure2.1.txt"));
            FileWriter f = new FileWriter("intermediate.txt");

            input = sc.nextLine();
            
            do {

                
                if (input.indexOf("START") != -1) {
                    StringTokenizer st = new StringTokenizer(input, " \t");
                    label = st.nextToken();
                    opcode = st.nextToken();
                    operand = st.nextToken();
                    loc = operand;
                    addloc(0);
                    f.write(loc + "\t" + label + "\t" + opcode + "\t" + operand + "\n");
                } else if (input.indexOf(".") != -1) {
                    f.write(input + "\n");
                } else {
                    StringTokenizer st = new StringTokenizer(input, " \t");
                    label = st.nextToken();
                    if (!stringMap.containsKey(label)) { // label 真的是 label
                        if (!sybMap.containsKey(label)) {
                            sybMap.put(label, loc);
                           
                        } else {
                            System.out.print("dup");
                            System.exit(0);
                        }
                        opcode = st.nextToken();
                        operand = st.nextToken();
                        if (stringMap.containsKey(opcode)) {
                            addloc(3);
                        } else if (opcode.equals("WORD")) {
                            addloc(3);
                        } else if (opcode.equals("RESW")) {

                            addloc(Integer.valueOf(operand) * 3);

                        } else if (opcode.equals("RESB")) {

                            addloc(Integer.valueOf(operand));
                        } else if (opcode.equals("BYTE")) {
                            if (operand.charAt(0) == 'C')
                                addloc(operand.substring(2, operand.length() - 1).length());
                            else
                                addloc(1);
                        } else {
                            System.out.println("err");
                            System.exit(0);
                        }
                        f.write(loc + "\t" + label + "\t" + opcode + "\t" + operand + "\n");
                    } else { // label讀進的就是op ;
                        opcode = "";
                        if (label.equals("RSUB")) {
                            addloc(3);
                            operand = "";
                        } else {
                            operand = st.nextToken();
                            if (stringMap.containsKey(label)) {
                                addloc(3);
                            } else if (label.equals("WORD")) {
                                addloc(3);
                            } else if (label.equals("RESW")) {

                                addloc(Integer.valueOf(operand) * 3);

                            } else if (label.equals("RESB")) {

                                addloc(Integer.valueOf(operand));
                            } else if (label.equals("BYTE")) {
                                if (operand.charAt(0) == 'C')
                                    addloc(operand.substring(2, operand.length() - 1).length());
                                else
                                    addloc(1);
                            } else {
                                System.out.println("err");
                                System.exit(0);
                            }
                        }
                        f.write(loc + "\t" + label + "\t" + opcode + "\t" + operand + "\n");
                    }

                }
                loc = nextloc;
                input = sc.nextLine();
            } while (sc.hasNextLine());
            f.write(input + "\n");
            f.close();

        } catch (FileNotFoundException e) {
            ;
        } catch (IOException e) {
            ;
        }
    }

    public static void addloc(int a) {
        int decimalValue = Integer.parseInt(loc, 16);

        // 將十進制數字加到十進制值上
        int resultDecimal = decimalValue + a;

        // 將結果轉換為16進制，確保結果是四位數
        nextloc = String.format("%04X", resultDecimal);

    }

    public static void pass2(){
        String check ; 
        String input ; 
        String loc ;
        String label ;
        String opcode ;
        String operand ;
        String obj ;
        try{
        FileWriter file = new FileWriter("output.txt") ;
        Scanner sc = new Scanner(new File("intermediate.txt")) ;
        input = sc.nextLine() ;
        do{
            if(input.indexOf("START")!=-1){
                file.write(input+"\n");
            }
            else if(input.indexOf(".")!=-1)
            {
                 file.write(input+"\n");
            }
            else{
                StringTokenizer st = new StringTokenizer(input, " \t") ;
                loc = st.nextToken() ;
                check = st.nextToken() ;
                if(sybMap.containsKey(check))
                {
                    label = check ;
                    opcode = st.nextToken() ;
                    operand = st.nextToken() ;
                    if(opcode.equals("WORD"))
                    {
                        obj = String.format("%06X",Integer.valueOf(operand)) ;
                    }
                    else if(opcode.equals("BYTE"))
                    {
                        if(operand.charAt(0)=='C')
                        {
                            StringBuilder hexRepresentation = new StringBuilder();
                            for (char character : operand.substring(2, operand.length()-1).toCharArray()) {
                                hexRepresentation.append(String.format("%02X", (int) character));
                            } 
                            obj = hexRepresentation.toString();
                        }
                        else
                        {
                            obj = operand.substring(2, operand.length()-1) ;
                        }
                    }
                    else if(opcode.equals("RESB")||opcode.equals("RESW"))
                    {
                      obj = "" ;  
                    }
                    else{
                        obj = stringMap.get(opcode)+sybMap.get(operand) ;
                       
                    }
                }
                else{
                    opcode = check ;
                    
                    if(opcode.equals("RSUB"))
                    {
                        obj = "4C0000" ;
                    }
                    else {
                        operand = st.nextToken() ;
                        if(operand.indexOf(",")!=-1)
                    {
                        String digit = sybMap.get(operand.substring(0, operand.indexOf(","))) ;
                        int decimalValue = Integer.parseInt(digit.substring(0, 1), 16)+8;
                        obj = stringMap.get(opcode)+String.format("%01X", decimalValue)+digit.substring(1);
                    } 
                    else
                    {
                       obj = stringMap.get(opcode)+sybMap.get(operand) ;  
                    }
                }


                }
                file.write(input+"\t"+obj+"\n") ;
            }
            input = sc.nextLine() ;
        }while(sc.hasNext()) ;
        file.write(input);
        file.close();
        sc.close();

        }catch(IOException e){
            ;
        }





    }

}