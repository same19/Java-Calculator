import java.util.*;
import java.lang.*;
import java.text.*;
import java.math.*;
import java.io.*;
import java.io.*;
import java.security.*;
import java.nio.*;
import java.nio.file.*;
class Calculator {
    public static void main(String[] args) {
        Calculator c = new Calculator();
        c.run();
    }
    private static String pi = "3.1415926535897932384626433";
    private static String e = "2.7182818284590452353602874";
    private static String phi = "1.6180339887498948482045868";
    private static String defaultA = "ans";
    private String A = defaultA;
    private static String defaultL = "last";
    private String L = defaultL;
    private static String scientificNotationTrigger = "sci"; //put into menu
    private int defaultScientificDecimals = 2;
    private static String welcomeMessage = "CALCULATOR:\nEnter expressions to calculate (Type \"\\menu\" for Menu or \"\\settings\" for Settings):";
    private int precision = 8;
    private boolean fullStack = false;
    private String MEMORY = precision+"&"+fullStack+"&"+true;
    private Throwable defaultError = new Exception("None");
    private ArrayList<Throwable> errors = new ArrayList<Throwable>();
    private Throwable lastError = defaultError;
    private Dict<String,Operation> functions;
    private Dict<String,BiOperation> biFunctions;
    private Dict<String,String> variables;
    private static String spaceLeft = "             ";
    public Calculator() {
        super();
        resetVariables();
        functions = new Dict<String,Operation>();
        biFunctions = new Dict<String,BiOperation>();
    }
    public void resetVariables() {
        A = defaultA;
        L = defaultL;
        precision = 8;
        fullStack = false;
        MEMORY = precision+"&"+fullStack+"&"+true;
        Expression.setAngleMode(true);
        variables = new Dict<String,String>();
        // variables.add("pi",pi);
        // variables.add("e",e);
        // variables.add("phi",phi);
        variables.add(defaultA,A);
        variables.add(defaultL,L);
        // variables.replaceAll("pi",pi);
        // variables.replaceAll("e",e);
        // variables.replaceAll("phi",phi);
        variables.replaceAll(defaultA,A);
        variables.replaceAll(defaultL,L);
    }
    private void clearConsole() {
        int len = 20;
        for (int i=0;i<len;i++) {
             System.out.print("\033[A");
        }
        for (int i=0;i<len;i++) {
            System.out.println("\n");
        }
        //System.out.println("___________________________________________________________________________________________________________________________________");
        // System.out.print("\033[H\033[2b");
        System.out.flush();
    }
    private void menu() {
        menu("");
    }
    private void printOverviewError(Throwable e) {
        System.out.println(e);
    }
    private void printError(Throwable e) {
        if (e.equals(defaultError)) {
            System.out.println(spaceLeft+"None");
        } else {
            StringWriter errors1 = new StringWriter();
            e.printStackTrace(new PrintWriter(errors1));
            String stackTrace = errors1.toString();
            String[] linesofError = stackTrace.split("\\r?\\n");
            int i;
            for (i=0;i<linesofError.length && i<20;i++) {
                System.out.println(linesofError[i]);
                if (i<linesofError.length-1) {
                    System.out.print(spaceLeft+"    ");
                }
            }
            if (i>19) {
                System.out.println(spaceLeft+"... For a full stack trace of this error, turn on Show Full Stack Trace in Settings.");

            }
        }
    }
    private void printFullError(Throwable e) {
        if (e.equals(defaultError)) {
            System.out.println(spaceLeft+"None");
        } else {
            StringWriter errors1 = new StringWriter();
            e.printStackTrace(new PrintWriter(errors1));
            String stackTrace = errors1.toString();
            String[] linesofError = stackTrace.split("\\r?\\n");
            int i;
            for (i=0;i<linesofError.length;i++) {
                System.out.println(linesofError[i]);
                if (i<linesofError.length-1) {
                    System.out.print(spaceLeft+"    ");
                }
            }
        }
    }
    private void showErrors() {
        showErrors("");
    }
    private void showErrors(String place) {
        Scanner input = new Scanner(System.in);
        String choice;
        boolean inError = true;
        while (inError) {
            clearConsole();
            System.out.println("ERRORS:");
            System.out.println("Press 1 to see all errors");
            System.out.println("Press 2 to see the last error");
            System.out.println("Press 3 to see the full diagnostic of all errors");
            System.out.println("Press 4 to see the full diagnostic of the last error");
            System.out.println("---------------------------------------------------");
            System.out.println("Press the return/enter key to return to Menu");
            if (place.equals("errors")) {
                choice = "1";
                place = "";
            } else if (place.equals("fullerrors")) {
                choice = "3";
                place = "";
            } else if (place.equals("error")) {
                choice = "2";
                place = "";
            } else if (place.equals("fullerror")) {
                choice = "4";
                place = "";
            } else {
                choice = input.nextLine();
            }
            clearConsole();
            if (choice.equals("1")) {
                System.out.println("ERRORS:");
                System.out.println("All errors: ");
                for (int j=0;j<errors.size();j++) {
                    String printing = (j+1)+": "+spaceLeft;
                    System.out.printf("%.13s",printing);
                    printOverviewError(errors.get(j));
                }
                if (errors.size()==0) {
                    System.out.println(spaceLeft+"None");
                }
                System.out.println("---------------------------------------------------");
                System.out.println("Press the return/enter key to return to Errors");
                choice = input.nextLine();
            } else if (choice.equals("3")) {
                System.out.println("ERRORS:");
                System.out.println("All errors: ");
                for (int j=0;j<errors.size();j++) {
                    String printing = (j+1)+": "+spaceLeft;
                    System.out.printf("%.13s",printing);
                    if (!fullStack) {
                        printError(errors.get(j));
                    } else {
                        printFullError(errors.get(j));
                    }
                }
                if (errors.size()==0) {
                    System.out.println(spaceLeft+"None");
                }
                System.out.println("---------------------------------------------------");
                System.out.println("Press the return/enter key to return to Errors");
                choice = input.nextLine();
            } else if (choice.equals("2")) {
                System.out.println("ERRORS:");
                System.out.println("Last Error: ");
                System.out.print(spaceLeft);
                printOverviewError(lastError);
                System.out.println("---------------------------------------------------");
                System.out.println("Press the return/enter key to return to Errors");
                choice = input.nextLine();
            } else if (choice.equals("4")) {
                System.out.println("ERRORS:");
                System.out.println("Last Error: ");
                System.out.print(spaceLeft);
                if (!fullStack) {
                    printError(lastError);
                } else {
                    printFullError(lastError);
                }
                System.out.println("---------------------------------------------------");
                System.out.println("Press the return/enter key to return to Errors");
                choice = input.nextLine();
            } else {
                inError = false;
            }
        }
    }
    private void menu(String place) {
        String inp1 = "0";
        String inp2;
        Scanner input = new Scanner(System.in);
        boolean inMenu = true;
        boolean repeated = false;
        String place1 = "";
        while (inMenu) {
            while (repeated || (!inp1.equals("1") && !inp1.equals("2") && !inp1.equals("3") && !inp1.equals("4") && !inp1.equals("5") && !inp1.equals("6") && !inp1.equals(""))) {
                clearConsole();
                System.out.println("MENU:");
                System.out.println("Press 1 for a list of available functions and how to use them");
                System.out.println("Press 2 for how to use variables and functions");
                System.out.println("Press 3 for Memory");
                System.out.println("Press 4 for Settings");
                System.out.println("Press 5 for past Errors");
                System.out.println("Press 6 to turn off Calculator");
                System.out.println("---------------------------------------------------");
                System.out.println("Press the return/enter key to exit menu.");
                if (place.equals("settings")) {
                    inp1 = "4";
                    place = "";
                } else if (place.equals("functionInfo")) {
                    inp1 = "1";
                    place = "";
                } else if (place.equals("varHelp")) {
                    inp1 = "2";
                    place = "";
                } else if (place.equals("memory") || place.equals("variables") || place.equals("functions") || place.equals("bifunctions")) {
                    inp1 = "3";
                    place = "";
                } else if (place.equals("error") || place.equals("errors") || place.equals("fullerror") || place.equals("fullerrors")) {
                    inp1 = "5";
                    place1 = new String(place);
                    place = "";
                } else {
                    inp1 = input.nextLine();
                }
                repeated=false;
            }
            repeated=true;
            clearConsole();
            if (inp1.equals("1")) {
                System.out.println("MENU:");
                System.out.println("a*b:            returns a multiplied by b");
                System.out.println("a/b:            returns a divided by b");
                System.out.println("a+b:            returns a added to b");
                System.out.println("a-b:            returns b subtracted from a");
                System.out.println("a%b, a mod b:   returns the modulo b residue of a");
                System.out.println("a log b:        returns the logarithm of b in base a - default value of a is 10");
                System.out.println("ln a:           returns the natural logarithm of a");
                System.out.println("a^b:            returns a multiplied by itself b times");
                System.out.println("a sqrt b:       returns b ^ (1/a) - default value of a is 2");
                System.out.println("|(a)|, abs(a):  returns the absolute value of a - when using the form |(a)|, parenthesis must be included inside the \"|\" signs");
                System.out.println("a!:             returns the factorial of a - if a is a non-integer, returns the gamma function of a");
                System.out.println("sin a:          returns the sine of angle a");
                System.out.println("cos a:          returns the cosine of angle a");
                System.out.println("tan a:          returns the tangent of angle a");
                System.out.println("arcsin a:       returns an angle x between -pi/2 and pi/2 radians inclusive, such that sin x is equivalent to a");
                System.out.println("arccos a:       returns an angle x between -pi/2 and pi/2 radians inclusive, such that cos x is equivalent to a");
                System.out.println("arctan a:       returns an angle x between -pi/2 and pi/2 radians inclusive, such that tan x is equivalent to a");
                System.out.println("csc a:          returns the cosecant of angle a");
                System.out.println("sec a:          returns the secant of angle a");
                System.out.println("cot a:          returns the cotangent of angle a");
                System.out.println("(a)(b)sum,f:    returns the sum of f(i) for all i, starting from i=a and increasing by 1 until i=b, after which it stops");
                System.out.println("f_(a), f,lim(a): returns the limit of the single variabled function f as x approaches a");
                System.out.println("f'(a):          returns the derivative of the single variabled function f at the value a");
                System.out.println("\nNote that the limit function gives an approximation, and is not always perfectly accurate");
                System.out.println("Note that to separate values, commas can be used in between the values, as shown in the limit description and the sum description");
                System.out.println("Note that taking the second derivative of a function whether directly or indirectly might result in an error");
                System.out.println("Note that for all angle to ratio trigonometric functions, a is in radians - use \"<function>(a deg)\" to measured a in degrees");

                System.out.println("---------------------------------------------------");
                System.out.println("Press the return/enter key to return to Menu");
                inp2 = input.nextLine();
                clearConsole();
            } else if (inp1.equals("2")) {
                System.out.println("MENU:");
                System.out.println("The predefined variable \""+defaultA+"\" holds the value of the last result.");
                System.out.println("The predefined variable \""+defaultL+"\" holds the value of the last entry.");
                System.out.println("To define a variable, use the form:");
                System.out.println(spaceLeft+"<name> = <value>");
                System.out.println("To define a single variabled function, use the form:");
                System.out.println(spaceLeft+"<name>() = <value in terms of x>");
                System.out.println("To define a two variabled function, use the form:");
                System.out.println(spaceLeft+"<name>() = <value in terms of x and y>");
                System.out.println("Examples:");
                System.out.println(spaceLeft+"a = 5+7*4");
                System.out.println(spaceLeft+"b() = 3x^2+2");
                System.out.println(spaceLeft+"c() = x^y + xy");
                System.out.println("To use a variable, just type the name in like a number.");
                System.out.println("To use a single variabled function, just type the name in and type the x value directly after");
                System.out.println("To use a two variabled function, type in the y value, then the name of the function, then the x value");
                System.out.println("Examples of using functions:");
                System.out.println(spaceLeft+"d()=4x");
                System.out.println(spaceLeft+"d(3)");
                System.out.println(spaceLeft+spaceLeft+"84");
                System.out.println(spaceLeft+"e()=2x+y");
                System.out.println(spaceLeft+"(3)e(1)");
                System.out.println(spaceLeft+spaceLeft+"5");
                System.out.println("To delete a variable, use the form:");
                System.out.println(spaceLeft+"<name> = \\");
                System.out.println("To delete a function, use the form:");
                System.out.println(spaceLeft+"<name>() = \\");
                System.out.println("To take a limit of a single variabled function as it approaches a certain value, see Available Functions");
                System.out.println("To take a derivative of a single variabled function, see Available Functions");
                System.out.println("\nNote that variable and function names might cause errors when one name contains another");
                System.out.println("Note that the variable names x, y, &, "+defaultL+", and "+defaultA+" are illegal");

                System.out.println("---------------------------------------------------");
                System.out.println("Press the return/enter key to return to Menu");
                inp2 = input.nextLine();
                clearConsole();
            } else if (inp1.equals("3")) {
                Scanner input2 = new Scanner(System.in);
                String inInput2;
                String choiceInSettings = "1";
                while (choiceInSettings.equals("1")||choiceInSettings.equals("2")||choiceInSettings.equals("3")||choiceInSettings.equals("4")||choiceInSettings.equals("5")||choiceInSettings.equals("6")) {
                    System.out.println("MEMORY:");
                    System.out.println("Press 1 to see all variables and their values");
                    System.out.println("Press 2 to see all functions and their values");
                    System.out.println("Press 3 to clear memory");
                    System.out.println("Press 4 to set a variable or function");
                    System.out.println("Press 5 to view current Memory shortcut");
                    System.out.println("Press 6 to set Memory to a memory shortcut");
                    System.out.println("---------------------------------------------------");
                    System.out.println("Press the return/enter key to return to Menu");
                    choiceInSettings = input2.nextLine();
                    clearConsole();
                    if (choiceInSettings.equals("1")) {
                        System.out.println("MEMORY:");
                        System.out.println("Variables:");
                        System.out.println("<variable name>: <value>");
                        variables.printInLine();
                        System.out.println("---------------------------------------------------");
                        System.out.println("Press the return/enter key to return to Memory");
                        inInput2 = input2.nextLine();
                    } else if (choiceInSettings.equals("2")) {
                        System.out.println("MEMORY:");
                        System.out.println("Functions:");
                        System.out.println("<function name>: <value>");
                        functions.printInLine();
                        biFunctions.printInLine();
                        System.out.println("---------------------------------------------------");
                        System.out.println("Press the return/enter key to return to Memory");
                        inInput2 = input2.nextLine();
                    } else if (choiceInSettings.equals("3")) {
                        System.out.println("MEMORY:");
                        System.out.println("Are you sure you want to clear the memory?");
                        inInput2 = input2.nextLine();
                        if (!inInput2.contains("no") && !inInput2.contains("cancel")) {
                            resetVariables();
                            System.out.println("---------------------------------------------------");
                            System.out.println("Press the return/enter key to return to Memory");
                            inInput2 = input2.nextLine();
                        }
                    } else if (choiceInSettings.equals("4")) {
                        inInput2 = "0";
                        while (!inInput2.contains("=")) {
                            clearConsole();
                            System.out.println("MEMORY:");
                            System.out.println("Set a variable or function:");
                            inInput2 = input2.nextLine();
                        }
                        String inMemorySetVar = setVariables(inInput2);
                        System.out.println(spaceLeft+inMemorySetVar);
                        System.out.println("---------------------------------------------------");
                        System.out.println("Press the return/enter key to return to Memory");
                        inInput2 = input2.nextLine();
                    } else if (choiceInSettings.equals("5")) {
                        System.out.println("MEMORY:");
                        System.out.println("The current encoded Memory shortcut is:");
                        System.out.println(Base64.getEncoder().encodeToString(MEMORY.getBytes()));
                        System.out.println("---------------------------------------------------");
                        System.out.println("Press the return/enter key to return to Memory");
                        inInput2 = input2.nextLine();
                    } else if (choiceInSettings.equals("6")) {
                        System.out.println("MEMORY:");
                        System.out.println("Enter the memory shortcut with which you would like to set Memory:");
                        Scanner input2_1 = new Scanner(System.in);
                        String in2_1 = input2_1.nextLine();
                        if (in2_1.contains("=")) {
                            setUpMemory(in2_1,false);
                            System.out.println("Memory set to shortcut: "+in2_1);
                        }
                        System.out.println("---------------------------------------------------");
                        System.out.println("Press the return/enter key to return to Memory");
                        in2_1 = input2_1.nextLine();
                    }
                    clearConsole();
                }
            } else if (inp1.equals("4")) {
                Scanner input2 = new Scanner(System.in);
                String inInput2;
                String choiceInSettings = "1";
                while (choiceInSettings.equals("1")||choiceInSettings.equals("2")||choiceInSettings.equals("3")) {
                    clearConsole();
                    System.out.println("SETTINGS:");
                    System.out.println("Press 1 to change default rounding precision");
                    System.out.println("Press 2 to switch Show Full Stack Trace");
                    System.out.println("Press 3 to switch radians/degree mode");
                    System.out.println("---------------------------------------------------");
                    System.out.println("Press the return/enter key to return to Menu");
                    choiceInSettings = input2.nextLine();
                    clearConsole();
                    if (choiceInSettings.equals("1")) {
                        System.out.println("SETTINGS:");
                        System.out.println("Enter the value to which you would like to change the rounding precision:");
                        Scanner input2_1 = new Scanner(System.in);
                        boolean done2_1 = false;
                        while (!done2_1) {
                            String in2_1 = input2_1.nextLine();
                            if (in2_1.equals("")) {
                                done2_1 = true;
                            } else if (Expression.isInteger(in2_1)) {
                                precision = (int) Double.parseDouble(in2_1);
                                System.out.println("Rounding precision is now "+precision+" places after the decimal");
                                int p;
                                for (p = 0;MEMORY.charAt(p)!='&'&&p<MEMORY.length();p++) {
                                    ;
                                }
                                MEMORY = precision+MEMORY.substring(p);
                                done2_1 = true;
                            }
                        }
                        System.out.println("---------------------------------------------------");
                        System.out.println("Press the return/enter key to return to Settings");
                        inInput2 = input2.nextLine();
                    } else if (choiceInSettings.equals("2")) {
                        System.out.println("SETTINGS:");
                        fullStack = !fullStack;
                        int p;
                        int p2=0;
                        boolean p1 = true;
                        for (p = 0;(p1||MEMORY.charAt(p)!='&')&&p<MEMORY.length();p++) {
                            if (MEMORY.charAt(p)=='&') {
                                p1 = false;
                                p2 = p+1;
                            }
                        }
                        MEMORY = MEMORY.substring(0,p2)+fullStack+MEMORY.substring(p);
                        System.out.println("Show Full Stack Trace changed to "+fullStack);
                        System.out.println("---------------------------------------------------");
                        System.out.println("Press the return/enter key to return to Settings");
                        inInput2 = input2.nextLine();
                    } else if (choiceInSettings.equals("3")) {
                        System.out.println("SETTINGS:");
                        System.out.println("Angles are now in "+Expression.changeAngleMode());
                        int p;
                        int p2 = 0;
                        int p1 = 0;
                        for (p = 0;(p1<2||MEMORY.charAt(p)!='&')&&p<MEMORY.length();p++) {
                            if (MEMORY.charAt(p)=='&') {
                                p2 = p+1;
                                p1++;
                            }
                        }
                        MEMORY = MEMORY.substring(0,p2)+Expression.getAngleMode()+MEMORY.substring(p);
                        System.out.println("---------------------------------------------------");
                        System.out.println("Press the return/enter key to return to Settings");
                        inInput2 = input2.nextLine();
                    }
                }
            } else if (inp1.equals("5")) {
                showErrors(place1);
            } else if (inp1.equals("6")) {
                System.out.println("MENU:");
                System.out.println("Are you sure you want to turn off Calculator?");
                String answer = input.nextLine();
                answer = answer.toLowerCase();
                if (!answer.contains("no") && !answer.contains("cancel")) {
                    clearConsole();
                    System.exit(1);
                }
            } else {
                inMenu=false;
                clearConsole();
            }
        }
    }
    public void run() {
    	resumeMemory();
        clearConsole();
        // Expression.stringToInt();
        Scanner s = new Scanner(System.in);
        System.out.println(welcomeMessage);
        String k = s.nextLine();
        String l;
        while (!k.equals("") && !k.equals("\\off") && !k.equals("\\quit") && !k.equals("\\exit") && !k.equals("\\turnOff")) {
            k = k.replaceAll(defaultL,L);
            L = "\\"+k;
            addToMemory(k);
            variables.replaceAll(defaultL,L);
            if (k.contains("=")) {
                try {
                    k = setVariables(k);
                } catch (Throwable t) {
                    lastError = t;
                    errors.add(t);
                    k = "ERROR: Invalid variable or function declaration: "+k+": For details about this error, type \"\\error\"";
                }
                System.out.println(spaceLeft+k);
            } else if (k.contains("\\")) {
                String k1 = k.substring(1).toLowerCase();
                if (k1.equals("clr") || k1.equals("cls") || k1.equals("clear")) {
                    resetVariables();
                    functions = new Dict<String,Operation>();
                    biFunctions = new Dict<String,BiOperation>();
                    clearConsole();
                    System.out.println(welcomeMessage);
                } else {
                    String k1Copy = new String(k1);
                    menu(k1Copy);
                    System.out.println(welcomeMessage);
                    // System.out.println(k);
                    // System.out.println(spaceLeft+k);
                }
            } else {
            	boolean scientificNotationOutput = false;
            	if (k.contains(scientificNotationTrigger)) {
            		scientificNotationOutput = true;
            		k = k.replaceAll(scientificNotationTrigger,"");
            	}
                l = calculateNumeralExpression(k,scientificNotationOutput);
                variables.replaceAll(defaultA,A);
                System.out.println(spaceLeft+l);
            }
            k = s.nextLine();
        }
        System.out.println("Are you sure you want to turn off Calculator?");
        String answer = s.nextLine();
        answer = answer.toLowerCase();
        if (answer.contains("no") || answer.contains("cancel")) {
            run();
        }
        clearConsole();
    }
    private String calculateNumeralExpression(String s) {
    	return calculateNumeralExpression(s, false);
    }
    private String calculateNumeralExpression(String s, boolean scientificNotation) {
        String s2 = new String(s);
        String aResult;
        Expression a = new Expression(s,variables,functions,biFunctions);
        try {
        	String unprocessed = a.getResult();
            aResult = processOutput(unprocessed,s, scientificNotation);
            A = unprocessed;//don't need extra error
        } catch(Throwable e) {
            try {
                String s1 = defaultA+s;
                a.setRaw(s1);
                a.parse();
                a.evaluate();
                String unprocessed = a.getResult();
                aResult = processOutput(unprocessed,s, scientificNotation);
                A = unprocessed;
            } catch(Throwable f) {
                //f.printStackTrace();
                lastError = f;
                errors.add(f);
                aResult = "ERROR: Invalid Expression: "+s2+": For details about this error, type \"\\error\"";
            }
        }
        return aResult;
    }
    private String setVariables(String k1) {
        String k = new String(k1);
        k.replaceAll("\\s","");
        k.replaceAll(" ","");
        String part1 = new String();
        String part2 = new String();
        boolean passedK = false;
        for (int i=0;i<k.length();i++) {
            if (k.charAt(i)=='=') {
                passedK = true;
            } else {
                if (passedK) {
                    part2 += k.charAt(i);
                } else {
                    part1 += k.charAt(i);
                }
            }
        }
        //System.out.println("firstpart: '"+part1+"'");
        if (part1.equals("x") || part1.equals("y") || part1.equals("&") || part1.equals(defaultA) || part1.equals(defaultL) || part1.equals(scientificNotationTrigger)) {
            String s = "ERROR: Invalid variable or method declaration: "+k1+": For details about this error, type \"\\error\"";
            lastError = new Error("Invalid variable or method declaration: "+k1+": Variable name \""+part1+"\" is illegal");
            errors.add(lastError);
            return s;
        }
        if (part2.contains("x") && part2.contains("y") && part1.contains("()")) {
            if (part2.equals("") || part2.contains("null") || part2.contains("\\")) {
                biFunctions.remove(part1.substring(0,part1.indexOf("(")));
            } else {
                String q = part1.substring(0,part1.indexOf("("));
                if (!biFunctions.contains(q)) {
                    BiOperation q1 = new BiOperation(part2,0,variables,functions,biFunctions);
                    biFunctions.add(q,q1);
                } else {
                    BiOperation q1 = new BiOperation(part2,0,variables,functions,biFunctions);
                    biFunctions.replaceAll(q,q1);
                }
            }
            variables.remove(part1.substring(0,part1.indexOf("(")));
            functions.remove(part1.substring(0,part1.indexOf("(")));
            return k;
        } else if (part1.contains("()")) {
            //System.out.println(part1 + " "+part1.length());
            if (part2.equals("") || part2.contains("null") || part2.contains("\\")) {
                functions.remove(part1.substring(0,part1.indexOf("(")));
            } else {
                String q = part1.substring(0,part1.indexOf("("));
                if (!functions.contains(q)) {
                    Operation q1 = new Operation(part2,variables,functions,biFunctions);
                    functions.add(q,q1);
                } else {
                    Operation q1 = new Operation(part2,variables,functions,biFunctions);
                    functions.replaceAll(q,q1);
                }
            }
            variables.remove(part1.substring(0,part1.indexOf("(")));
            biFunctions.remove(part1.substring(0,part1.indexOf("(")));
            return k;
        }
        if (part2.equals("") || part2.contains("null") || part2.contains("\\")) {
            variables.remove(part1);
        } else {
            Expression a = new Expression(part2,variables,functions,biFunctions);
            part2 = a.getResult();
            if (variables.contains(part1)) {
                variables.replaceAll(part1,part2);
            } else {
                variables.add(part1,part2);
            }
        }
        return k;
        
    }
    private String processOutput(String s, String in) {
    	return processOutput(s, in, false);
    }
    private String processOutput(String s,String in, boolean scientificNotation) {
        if (Expression.isDouble(s)) {
            if (scientificNotation) {
            	s=String.format("%."+ (new Integer(defaultScientificDecimals)).toString()+"E",Double.parseDouble(s));
            } else if (Double.parseDouble(s) == (int) Double.parseDouble(s)) {
                s = String.valueOf((int) Double.parseDouble(s));
            } else {
                String dfString = "#.";
                for (int i=0;i<precision;i++) {
                    dfString += "#";
                }
                DecimalFormat df = new DecimalFormat(dfString);
                df.setRoundingMode(RoundingMode.CEILING);
                s = String.valueOf(df.format(Double.parseDouble(s)));
                if (s.equals("-0")) {
                    s="0";
                }
            }
        }
        if (s.contains("ERROR")) {
            s += ": For details about this error, type \"\\error\"";
        }
        return s;
        
    }
    private void setUpMemory(String m, boolean t) {
        if (t) {
            setUpMemory(m);
        } else {
            byte[] b = Base64.getDecoder().decode(m);
            String d = new String(b);
            setUpMemory(d);
        }
    }
    private void addToMemory(String s) {
    	setMemory(MEMORY+"&"+s);
    }
    private void setMemory(String s) {
    	MEMORY = s;
    	try {
    	  File f = new File("memory.txt");
	      //f.createNewFile();
	      FileWriter writer = new FileWriter("memory.txt");
	      writer.write(Base64.getEncoder().encodeToString(MEMORY.getBytes()));
	      writer.close();
	    } catch (IOException e) {
	    }
    }
    private void resumeMemory() {
    	try {
	      File f = new File("memory.txt");
	      Scanner reader = new Scanner(f);
	      if (reader.hasNextLine()) {
	        setUpMemory(reader.nextLine(), false);
	      }
	      reader.close();
	    } catch (FileNotFoundException e) {
	    }
    }
    private void setUpMemory(String m) {
        resetVariables();
        setMemory(m);
        String l;
        String[] mList = m.split("&");
        precision = Integer.parseInt(mList[0]);
        fullStack = Boolean.parseBoolean(mList[1]);
        Expression.setAngleMode(Boolean.parseBoolean(mList[2]));
        if (mList.length<4) {
            return;
        }
        for (int j=3;j<mList.length;j++) {
            String k = mList[j];
            k = k.replaceAll(defaultL,L);
            L = "\\"+k;
            variables.replaceAll(defaultL,L);
            if (k.contains("=")) {
                try {
                    k = setVariables(k);
                } catch (Throwable t) {
                    lastError = t;
                    errors.add(t);
                    k = "ERROR: Invalid variable or function declaration: "+k+": For details about this error, type \"\\error\"";
                }
            } else if (k.contains("\\")) {
                ;
            } else {
                l = calculateNumeralExpression(k);
                variables.replaceAll(defaultA,A);
            }
        }
    }
}
