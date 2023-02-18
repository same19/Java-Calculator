import java.util.*;
import java.lang.*;
import java.text.*;
import java.math.*;
class Expression {
    private static int minChar = 10000;
    private static final String pi = "3.1415926535897932384626433";
    private static final String e = "2.7182818284590452353602874";
    private static final String phi = "1.6180339887498948482045868";
    private static int limitAccuracy = 5;
    private static boolean angleMode = true;
    private static final double limaccuracy2 = 100000000.0;
    private String origRaw;
    private String raw;
    private ArrayList<String> processed;
    private boolean isEvaluated;
    private String evaluation;
    private static boolean isTesting = false;
    private static String[] keywords = {"sqrt","log","ln","mod","\\*\\*","pi","e","phi"};
    private static String[] keyValues = {"√","~","`","%","^","π","e","ϕ"};
    private Dict<String,String> variables;
    private Dict<String,Operation> functions;
    private Dict<String,BiOperation> biFunctions;

    //                            option v: √,   ~,   `,    ^,  option p: π
    //private static char availableOperations = {'`','~','\'','^','*','-','+'};
    //raw = raw.replaceAll("\\s","");
        // raw = raw.replaceAll("~",""); // log
        // raw = raw.replaceAll("`",""); // ln
        // raw = raw.replaceAll("sqrt","√");
        // raw = raw.replaceAll("log","~");
        // raw = raw.replaceAll("ln","`");
        // raw = raw.replaceAll("\\*\\*","^");
        // raw = raw.replaceAll("pi","π");
        // raw = raw.replaceAll("e","e");
        // raw = raw.replaceAll("phi","ϕ");
    public Expression() {
        this("");
    }
    
    public Expression(List<String> processed) {
        this(processed,new Dict<String,String>(),new Dict<String,Operation>(),new Dict<String,BiOperation>());
    }
    public Expression(List<String> processed,Dict<String,String> variables,Dict<String,Operation> functions,Dict<String,BiOperation> biFunctions) {
        super();
        isEvaluated=false;
        origRaw = new String();
        this.processed = new ArrayList<String>(processed);
        this.variables = variables;
        this.functions = functions;
        this.biFunctions = biFunctions;
    }
    public Expression(String raw) {
        this(raw,new Dict<String,String>(),new Dict<String,Operation>(),new Dict<String,BiOperation>());
    }
    public Expression(String raw, Dict<String,String> variables) {
        this(raw,variables,new Dict<String,Operation>(),new Dict<String,BiOperation>());
    }
    public Expression(String raw, Dict<String,String> variables, Dict<String,Operation> functions, Dict<String,BiOperation> biFunctions) {
        super();
        this.raw = raw;
        this.origRaw = raw;
        isEvaluated=false;
        processed = new ArrayList<String>();
        this.variables = variables;
        this.functions = functions;
        this.biFunctions = biFunctions;
        parse();
    }
    public static boolean getAngleMode() {
        return angleMode;
    }
    public static void setAngleMode(boolean b) {
        angleMode = b;
    }
    public static String changeAngleMode() {
        angleMode = !angleMode;
        if (angleMode) {
            return "Radians";
        } else {
            return "Degrees";
        }
    }
    public void setLimitAccuracy(int n) {
        limitAccuracy = n;
    }
    private ArrayList<String> insertArray(int start,int end, ArrayList<String> arr1, ArrayList<String> arr2) { //start and end are both inclusive
        ArrayList<String> arr3 = new ArrayList<String>();
        int i=0;
        for (;i<start;i++) {
            arr3.add(arr1.get(i));
        }
        for (;i<start+arr2.size();i++) {
            arr3.add(arr2.get(i-start));
        }
        for (;i<arr2.size()+arr1.size()-1-end+start;i++) {
            arr3.add(arr1.get(i+end+1-start-arr2.size()));
        }
        arr1 = new ArrayList<String>(arr3);
        return arr1;
    }
    private ArrayList<String> insertArray(int start, ArrayList<String> arr1, ArrayList<String> arr2) {
        arr1 = insertArray(start, start, arr1, arr2);
        return arr1;
    }
    public double processLimitResults(double[] results) {
        //double[] diffs = new double[results.length-1];
        // for (int i=0;i<diffs.length;i++) {
        //     diffs[i] = Math.abs(results[i+1]-results[i]);
        // }
        double d = Math.round(results[results.length-1]*limaccuracy2)/limaccuracy2;
        if (isTesting) {
            System.out.println("processing: "+results[results.length-1]);
            System.out.println(d);
        }
        if (d>1000000 || d<-1000000) {
            return d/0.0;
        }
        return d;
    }
    public double bottomLimit(double x, Operation o) {
        double x1 = x-0.000001;
        // double x2 = x-0.00001;
        // while (x2 < x && o.evaluate(x2) != 0.0/0.0) {
        //     x1 = x2;
        //     x2 = (x2-x)/10+x;
        // }
        // double[] results = new double[limitAccuracy];
        // for (int i=0;i<limitAccuracy;i++) {
        //     results[i] = o.evaluate(x1);
        //     x1 = (x1-x)/10+x;
        // }
        // if (isTesting) {
        //     System.out.println();
        //     System.out.print("[");
        //     for (int i=0;i<results.length;i++) {
        //         System.out.print(results[i]+", ");
        //     }
        //     System.out.println("]");
        // }
        // return (processLimitResults(results));
        return Math.round(o.evaluate(x1)*limaccuracy2)/limaccuracy2;
    }
    public double infiniteLimit(double x, Operation o) {
        double x1;
        double[] results = new double[limitAccuracy];
        if (x>0) {
            x1=100;
            for (int i=0;i<limitAccuracy;i++) {
                results[i] = o.evaluate(x1);
                x1 = x1*1000;
            }
            return (processLimitResults(results));
        } else {
            x1=-100;
            for (int i=0;i<limitAccuracy;i++) {
                results[i] = o.evaluate(x1);
                x1 = x1*1000;
            }
            return (processLimitResults(results));
        }
    }
    public double topLimit(double x, Operation o) {
        // System.out.println("X, then evaluation:   "+o);
        // System.out.print("[");
        double x1 = x+0.000001;
        // double x2 = x+0.00001;
        // while (x2 > x) {
        //     x1 = x2;
        //     x2 = (x2-x)/10+x;
        // }
        // double[] results = new double[limitAccuracy];
        // for (int i=0;i<limitAccuracy;i++) {
        //     System.out.print(x1+", ");
        //     results[i] = o.evaluate(x1);
        //     x1 = (x1-x)/10+x;
        // }
        // System.out.println("]");
        // //if (isTesting) {
            
        //     System.out.print("[");
        //     for (int i=0;i<results.length;i++) {
        //         System.out.print(results[i]+", ");
        //     }
        //     System.out.println("]");
        // //}
        // return (processLimitResults(results));
        return Math.round(o.evaluate(x1)*limaccuracy2)/limaccuracy2;
    }
    public double limit(double x, Operation o) {
        double bottom;
        double top;
        if (x==1.0/0 || x==-1.0/0) {
            top = infiniteLimit(x,o);
            bottom = top;
        } else {
            top = topLimit(x,o);
            bottom = bottomLimit(x,o);
        }
        if (isTesting) {
            System.out.println(origRaw);
            System.out.println("bottom: "+bottom);
            System.out.println("top: "+top);
        }
        if (Math.abs(top-bottom)<=0.1) {
            return (top+bottom)/2;
        } else {
            return 0.0/0;
        }
    }
    private ArrayList<String> insertArray(int start, int end, ArrayList<String> arr1, String arr2) {
        ArrayList<String> k = new ArrayList<String>();
        k.add(arr2);
        arr1 = insertArray(start, end, arr1, k);
        return arr1;
    }
    public String evaluate() {
        return evaluate(processed);
    }
    public String evaluate(ArrayList<String> l) {
        //System.out.println("\nEVALUATING: "+origRaw);
        if (!processed.equals(null)) {
            if (processed.size()==1) {
                isEvaluated=true;
                evaluation = processed.get(0);
                return processed.get(0);
            }
        }
        evalParenthesis();
        //evalSpecials0();
        //evalSpecials1();
        //evalExponents();
        //evalMultiplication();
        //evalAddition();
        if (processed.size()==1) {
            evaluation = processed.get(0);
            isEvaluated=true;
            return evaluation;
        } else {
            if (processed.equals(l)) {
                return toRaw();
            } else {
                return evaluate(processed);
            }
        }
    }
    private int findEndParens(int index) {
        int k=1;
        int i;
        for (i=index+1;k>0 && i<processed.size();i++) {
            if (processed.get(i).equals("(")) {
                k++;
            } else if (processed.get(i).equals(")")) {
                k--;
            }
        }
        if (k==0) {
            return i;
        }
        return index+1;
    }
    private void evalParenthesis() {
        Expression a;
        int end;
        List<String> r;
        for (int i=0;i<processed.size();i++) {
            // System.out.println(processed);
            if (processed.get(i).equals("(")) {
                //System.out.println(origRaw+": "+processed);
                end = findEndParens(i);
                r = processed.subList(i+1,end-1);
                a = new Expression(r,variables,functions,biFunctions);
                String t = a.evaluate();
                processed = insertArray(i,end-1,processed,t);
            }
        }
        evalSpecials0();
    }
    private double log(double b, double in) {
        return (Math.log(in)/Math.log(b));
    }
    private double log(double in) {
        return (Math.log(in));
    }
    private static double logGamma(double x) {
        double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
        double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
                       + 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
                       +  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
        return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
    }
    private static double gamma(double x) { return Math.exp(logGamma(x)); }
    private double factorial(double n) {
        if (isInteger(String.valueOf(n)) && n>0) {
            return factorial((int)n);
        }
        return gamma(n+1);
    }
    public double factorial(int n) {
        double total = 1;
        for (int i=1;i<=n;i++) {
            total *= i;
        }
        return total;
    }
    private double correctOppAngle(double n) {
        if (angleMode) {
            return n;
        } else {
            return n*180/Double.parseDouble(pi);
        }
    }
    private double correctAngle(double n) {
        if (angleMode) {
            return n;
        } else {
            return n*Double.parseDouble(pi)/180;
        }
    }
    private double sin(double n) {
        return Math.sin(correctAngle(n));
    }
    private double cos(double n) {
        return Math.cos(correctAngle(n));
    }
    private double tan(double n) {
        return correctOppAngle(Math.tan(correctAngle(n)));
    }
    private double arcsin(double n) {
        return correctOppAngle(Math.asin(n));
    }
    private double arccos(double n) {
        return correctOppAngle(Math.acos(n));
    }
    private double arctan(double n) {
        return correctAngle(Math.atan(n));
    }
    private double csc(double n) {
        return 1/Math.sin(correctAngle(n));
    }
    private double sec(double n) {
        return 1/Math.cos(correctAngle(n));
    }
    private double cot(double n) {
        return 1/Math.tan(correctAngle(n));
    }
    public double sum(int a, int b, Operation o) {
        double s = 0;
        for (double i=a;i<=b;i++) {
            s += o.evaluate(i);
        }
        return s;
    }
    private void evalSpecials0() {
        double powered;
        for (int i=0;i<processed.size();i++) {
            //System.out.println(origRaw+": "+processed);
            if (processed.get(i).equals("_")) {
                if (i>0) {
                    if (functions.contains(processed.get(i-1)) && isDouble(processed.get(i+1))) {
                        powered = limit(Double.parseDouble(processed.get(i+1)),functions.get(processed.get(i-1)));
                        processed = insertArray(i-1,i+1,processed,String.valueOf(powered));
                    } else {
                        processed.remove(i);
                    }
                } else {
                    processed.remove(i);
                }
                evalParenthesis();
            } else if (processed.get(i).equals("'")) {
                if (i>0) {
                    if (functions.contains(processed.get(i-1)) && isDouble(processed.get(i+1))) {
                        powered = functions.get(processed.get(i-1)).derivative(Double.parseDouble(processed.get(i+1)));
                        processed = insertArray(i-1,i+1,processed,String.valueOf(powered));
                    } else {
                        processed.remove(i);
                    }
                } else {
                    processed.remove(i);
                }
                evalParenthesis();
            } else if (processed.get(i).equals("∑")) {
                System.out.println(processed);
                if (i>1 && i<processed.size()-1) {
                    if (isInteger(processed.get(i-2))&&isInteger(processed.get(i-1))&&functions.contains(processed.get(i+1))) {
                        powered = sum((int)Double.parseDouble(processed.get(i-2)),(int)Double.parseDouble(processed.get(i-1)),functions.get(processed.get(i+1)));
                        processed = insertArray(i-2,i+1,processed,String.valueOf(powered));
                    } else {
                        processed.remove(i);
                    }
                } else {
                    processed.remove(i);
                }
            }
        }
        evalSpecials1();
    }
    private void evalSpecials1() {
        double powered;
        for (int i=processed.size()-1;i>=0;i--) {
            //System.out.println(origRaw+": "+processed);
            if (i<processed.size()) {
                if (processed.get(i).equals("~")) {
                    if (i>0) {
                        if (isDouble(processed.get(i-1)) && isDouble(processed.get(i+1))) {
                            powered = log( Double.parseDouble(processed.get(i-1)) , (Double.parseDouble(processed.get(i+1))) );
                            processed = insertArray(i-1,i+1,processed,String.valueOf(powered));
                        } else if (isDouble(processed.get(i+1))) {
                            powered = log(10.0,Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    } else {
                        if (isDouble(processed.get(i+1))) {
                            powered = log(10.0,Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    }
                    evalParenthesis();
                } else if (processed.get(i).equals("`")) {
                    if (isDouble(processed.get(i+1))) {
                        powered = log(Double.parseDouble(processed.get(i+1)));
                        processed = insertArray(i,i+1,processed,String.valueOf(powered));
                    } else {
                        processed.remove(i);
                    }
                    evalParenthesis();
                } else if (processed.get(i).equals("!")) {
                    if (i==0) {
                        processed.remove(i);
                    } else {
                        if (isDouble(processed.get(i-1))) {
                            powered = factorial(Double.parseDouble(processed.get(i-1)));
                            processed = insertArray(i-1,i,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    }
                    evalParenthesis();
                } else if (processed.get(i).equals("¡")) {
                    if (i==processed.size()-1) {
                        processed.remove(i);
                    } else {
                        if (isDouble(processed.get(i+1))) {
                            powered = sin(Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    }
                    evalParenthesis();
                }else if (processed.get(i).equals("™")) {
                    if (i==processed.size()-1) {
                        processed.remove(i);
                    } else {
                        if (isDouble(processed.get(i+1))) {
                            powered = cos(Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    }
                    evalParenthesis();
                }else if (processed.get(i).equals("£")) {
                    if (i==processed.size()-1) {
                        processed.remove(i);
                    } else {
                        if (isDouble(processed.get(i+1))) {
                            powered = tan(Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    }
                    evalParenthesis();
                }else if (processed.get(i).equals("¢")) {
                    if (i==processed.size()-1) {
                        processed.remove(i);
                    } else {
                        if (isDouble(processed.get(i+1))) {
                            powered = arcsin(Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    }
                    evalParenthesis();
                }else if (processed.get(i).equals("§")) {
                    if (i==processed.size()-1) {
                        processed.remove(i);
                    } else {
                        if (isDouble(processed.get(i+1))) {
                            powered = arccos(Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    }
                    evalParenthesis();
                }else if (processed.get(i).equals("¶")) {
                    if (i==processed.size()-1) {
                        processed.remove(i);
                    } else {
                        if (isDouble(processed.get(i+1))) {
                            powered = arctan(Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    }
                    evalParenthesis();
                }else if (processed.get(i).equals("•")) {
                    if (i==processed.size()-1) {
                        processed.remove(i);
                    } else {
                        if (isDouble(processed.get(i+1))) {
                            powered = csc(Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    }
                    evalParenthesis();
                }else if (processed.get(i).equals("ª")) {
                    if (i==processed.size()-1) {
                        processed.remove(i);
                    } else {
                        if (isDouble(processed.get(i+1))) {
                            powered = sec(Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    }
                    evalParenthesis();
                }else if (processed.get(i).equals("@")) {
                    if (i==processed.size()-1) {
                        processed.remove(i);
                    } else {
                        if (isDouble(processed.get(i+1))) {
                            powered = cot(Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    }
                    evalParenthesis();
                } else if (processed.get(i).equals("-")) {
                    if (i==0) {
                        if (isDouble(processed.get(i+1))) {
                            powered = 0 - Double.parseDouble(processed.get(i+1));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                            evalParenthesis();
                        }
                    } else {
                        if (isTesting) {
                            System.out.println(processed.get(i-1));
                            System.out.println(isDouble(processed.get(i-1)));
                        }
                        if (isDouble(processed.get(i+1)) && !isDouble(processed.get(i-1))) {
                            powered = 0 - Double.parseDouble(processed.get(i+1));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        }
                    }
                } else if (processed.get(i).equals("+")) {
                    if (i==0) {
                        if (isDouble(processed.get(i+1))) {
                            powered = 0 + Double.parseDouble(processed.get(i+1));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                            evalParenthesis();
                        }
                    } else {
                        if (isTesting) {
                            System.out.println(processed.get(i-1));
                            System.out.println(isDouble(processed.get(i-1)));
                        }
                        if (isDouble(processed.get(i+1)) && !isDouble(processed.get(i-1))) {
                            powered = 0 + Double.parseDouble(processed.get(i+1));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        }
                    }
                } else if (functions.contains(processed.get(i))) {
                    Operation o = functions.get(processed.get(i));
                    if (isDouble(processed.get(i+1))) {
                        powered = o.evaluate(Double.parseDouble(processed.get(i+1)));
                        processed = insertArray(i,i+1,processed,String.valueOf(powered));
                    } else {
                        processed.remove(i);
                    }
                    evalParenthesis();
                } else if (biFunctions.contains(processed.get(i))) {
                    BiOperation o = biFunctions.get(processed.get(i));
                    if (i==0) {
                        if (isDouble(processed.get(i+1))) {
                            powered = o.evaluate(Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    } else {
                        if (isTesting) {
                            System.out.println(processed.get(i-1));
                            System.out.println(isDouble(processed.get(i-1)));
                        }
                        if (isDouble(processed.get(i+1)) && isDouble(processed.get(i-1))) {
                            powered = o.evaluate(Double.parseDouble(processed.get(i+1)),Double.parseDouble(processed.get(i-1)));
                            processed = insertArray(i-1,i+1,processed,String.valueOf(powered));
                        } else if (isDouble(processed.get(i+1))) {
                            powered = o.evaluate(Double.parseDouble(processed.get(i+1)));
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        }
                    }
                    evalParenthesis();
                }
            }
        }
        evalExponents();
    }
    private void evalExponents() {
        
        double powered;
        for (int i=processed.size()-1;i>=0;i--) {
            //System.out.println(origRaw+": "+processed);
            if (i<processed.size()) {
                if (processed.get(i).equals("√")) {
                    if (i>0) {
                        if (isDouble(processed.get(i-1)) && isDouble(processed.get(i+1))) {
                            powered = Math.pow( Double.parseDouble(processed.get(i+1)) , 1/(Double.parseDouble(processed.get(i-1))) );
                            processed = insertArray(i-1,i+1,processed,String.valueOf(powered));
                        } else if (isDouble(processed.get(i+1))) {
                            powered = Math.pow( (Double.parseDouble(processed.get(i+1))) , 0.5 );
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    } else {
                        if (isDouble(processed.get(i+1))) {
                            powered = Math.pow( (Double.parseDouble(processed.get(i+1))) , 0.5 );
                            processed = insertArray(i,i+1,processed,String.valueOf(powered));
                        } else {
                            processed.remove(i);
                        }
                    }
                    evalParenthesis();
                } else if (processed.get(i).equals("^")) {
                    if (isDouble(processed.get(i-1)) && isDouble(processed.get(i+1))) {
                        powered = Math.pow( Double.parseDouble(processed.get(i-1)) , (Double.parseDouble(processed.get(i+1))) );
                        processed = insertArray(i-1,i+1,processed,String.valueOf(powered));
                    } else {
                        processed.remove(i);
                    }
                    evalParenthesis();
                }
            }
        }
        evalMultiplication();
    }
    private void evalMultiplication() {
        double multiplied;
        for (int i=0;i<processed.size()-1;i++) {
            if (processed.get(i).equals("*")) {
                if (isDouble(processed.get(i-1)) && isDouble(processed.get(i+1))) {
                    multiplied = Double.parseDouble(processed.get(i-1)) * Double.parseDouble(processed.get(i+1));
                    processed = insertArray(i-1,i+1,processed,String.valueOf(multiplied));
                } else {
                    processed.remove(i);
                }
                evalMultiplication();
            } else if (processed.get(i).equals("/")) {
                if (isDouble(processed.get(i-1)) && isDouble(processed.get(i+1))) {
                    multiplied = Double.parseDouble(processed.get(i-1)) / Double.parseDouble(processed.get(i+1));
                    processed = insertArray(i-1,i+1,processed,String.valueOf(multiplied));
                } else {
                    processed.remove(i);
                }
                evalMultiplication();
            } else if (isDouble(processed.get(i)) && isDouble(processed.get(i+1))) {
                multiplied = Double.parseDouble(processed.get(i)) * Double.parseDouble(processed.get(i+1));
                processed = insertArray(i,i+1,processed,String.valueOf(multiplied));
                evalMultiplication();
            } else if (processed.get(i).equals("%")) {
                if (isDouble(processed.get(i-1)) && isDouble(processed.get(i+1))) {
                    multiplied = Double.parseDouble(processed.get(i-1)) % Double.parseDouble(processed.get(i+1));
                    multiplied = (multiplied+Double.parseDouble(processed.get(i+1))) % Double.parseDouble(processed.get(i+1));
                    processed = insertArray(i-1,i+1,processed,String.valueOf(multiplied));
                } else {
                    processed.remove(i);
                }
                evalMultiplication();
            }
        }
        evalAddition();
    }
    private void evalAddition() {
        double added;
        for (int i=0;i<processed.size();i++) {
            //System.out.println(origRaw+": "+processed);
            if (processed.get(i).equals("+")) {
                if (i>0) {
                    if (isDouble(processed.get(i-1)) && isDouble(processed.get(i+1))) {
                        added = Double.parseDouble(processed.get(i-1)) + Double.parseDouble(processed.get(i+1));
                        processed = insertArray(i-1,i+1,processed,String.valueOf(added));
                    } else if (isDouble(processed.get(i+1))) {
                        added = 0 + Double.parseDouble(processed.get(i+1));
                        processed = insertArray(i,i+1,processed,String.valueOf(added));
                    } else {
                        processed.remove(i);
                    }
                } else {
                    if (isDouble(processed.get(i+1))) {
                        added = 0 + Double.parseDouble(processed.get(i+1));
                        processed = insertArray(i,i+1,processed,String.valueOf(added));
                    } else {
                        processed.remove(i);
                    }
                }
                evalAddition();
            } else if (processed.get(i).equals("-")) {
                if (i>0) {
                    if (isDouble(processed.get(i-1)) && isDouble(processed.get(i+1))) {
                        added = Double.parseDouble(processed.get(i-1)) - Double.parseDouble(processed.get(i+1));
                        processed = insertArray(i-1,i+1,processed,String.valueOf(added));
                    } else if (isDouble(processed.get(i+1))) {
                        added = 0 - Double.parseDouble(processed.get(i+1));
                        processed = insertArray(i,i+1,processed,String.valueOf(added));
                    } else {
                        processed.remove(i);
                    }
                } else {
                    if (isDouble(processed.get(i+1))) {
                        added = 0 - Double.parseDouble(processed.get(i+1));
                        processed = insertArray(i,i+1,processed,String.valueOf(added));
                    } else {
                        processed.remove(i);
                    }
                }
                evalAddition();
            }
        }
    }
    public static int[] specialChars() {
        String[] r = {"√","~","`","^","π","e","ϕ","%","¢","§","¶","¡","™","£","•","ª","@","∞","∑","*","(",")","-","+","!",".","/","|","1","2","3","4","5","6","7","8","9","0"};
        int[] q = new int[r.length];
        for (int i=0;i<r.length;i++) {
            q[i]=(int) (r[i]).charAt(0);
        }
        return q;
    }
    public void parse() {
        processed = new ArrayList<String>();
        raw = raw.replaceAll("degrees","(pi/180)");
        raw = raw.replaceAll("deg","(pi/180)");
        raw = raw.replaceAll("radians","(180/pi)");
        raw = raw.replaceAll("rad","(180/pi)");
        raw = raw.replaceAll("\\s","");
        raw = raw.replaceAll("sqrt","√");
        raw = raw.replaceAll("log","~");
        raw = raw.replaceAll("ln","`");
        raw = raw.replaceAll("\\*\\*","^");
        raw = raw.replaceAll("pi","π");
        raw = raw.replaceAll("e","e");
        raw = raw.replaceAll("phi","ϕ");
        raw = raw.replaceAll("mod","%");
//"√","~","`","^","π","e","ϕ","%","¢","§","¶","¡","™","£","•","ª","@","∞","∑","*","/","+","|","-","(",")","%",".","!",""
        raw = raw.replaceAll("arcsin","¢");
        raw = raw.replaceAll("arccos","§");
        raw = raw.replaceAll("arctan","¶");
        raw = raw.replaceAll("sin","¡"); 
        raw = raw.replaceAll("cos","™");
        raw = raw.replaceAll("tan","£");
        raw = raw.replaceAll("csc","•");
        raw = raw.replaceAll("sec","ª");
        raw = raw.replaceAll("cot","@");

        raw = raw.replaceAll("limit","_");
        raw = raw.replaceAll("lim","_");
        raw = raw.replaceAll("sum","∑");
        raw = raw.replaceAll("sigma","∑");
        raw = raw.replaceAll("Infinity","∞");
        raw = raw.replaceAll("infinity","∞");
        raw = raw.replaceAll("inf","∞");
        raw = raw.replaceAll("answer","ans");
        Dict<String,String> varSymbols = new Dict<String,String>();
        int j;
        for (int i=0;i<variables.size() && i<1000;i++) {
            j= minChar+i;
            varSymbols.add(String.valueOf((char)j),variables.values(i));
            raw = raw.replaceAll(variables.keys(i),String.valueOf((char)j));
        }
        Dict<String,String> functSymbols = new Dict<String,String>();
        for (int i=0;i<functions.size() && i<1000;i++) {
            j=minChar+1000+i;
            functSymbols.add(String.valueOf((char)j),functions.keys(i));
            raw = raw.replaceAll(functions.keys(i),String.valueOf((char)j));
        }
        Dict<String,String> biFunctSymbols = new Dict<String,String>();
        for (int i=0;i<biFunctions.size() && i<1000;i++) {
            j=minChar+2000+i;
            biFunctSymbols.add(String.valueOf((char)j),biFunctions.keys(i));
            raw = raw.replaceAll(biFunctions.keys(i),String.valueOf((char)j));
        }
        String k = "";
        if (isTesting) {
            System.out.println("varSymbols: "+varSymbols);
            System.out.println(raw+" printed raw");
        }
        for (int i=0;i<raw.length();i++) {
            if (isDouble(raw.charAt(i))||raw.charAt(i)=='.') {
                int iHolder = i;
                String k1 = new String(k);
                for (int i1 = i;i1<raw.length();i1++) {
                    k1 += raw.charAt(i1);
                    if (isDouble(k1)) {
                        iHolder = i1;
                    }
                }
                k = raw.substring(i,iHolder+1);
                if (k.equals(".")) {
                    k="";
                } else {
                    i = iHolder;
                }
            }
            if (!k.equals("")) {
                processed.add(k);
            } else if (raw.charAt(i) != ' ' && raw.charAt(i) != ',') {
                processed.add(String.valueOf(raw.charAt(i)));
            }
            k="";
        }
        if (!k.equals("")) {
            processed.add(k);
            k="";
        }
        for (int i=0;i<processed.size();i++) {
            if (processed.get(i).equals("π")) {
                processed.set(i,pi);
            } else if (processed.get(i).equals("e")) {
                processed.set(i,e);
            } else if (processed.get(i).equals("ϕ")) {
                processed.set(i,phi);
            } else if (processed.get(i).equals("∞")) {
                processed.set(i,String.valueOf(1.0/0));
            } else if (functSymbols.contains(processed.get(i))) {
                processed.set(i,functSymbols.get(processed.get(i)));
            } else if (biFunctSymbols.contains(processed.get(i))) {
                processed.set(i,biFunctSymbols.get(processed.get(i)));
            }else if (varSymbols.contains(processed.get(i))) {
                processed.set(i,varSymbols.get(processed.get(i)));
            }
        }
        if (isTesting) {
            System.out.println(processed+" printed processed");
        }
        //System.out.println(processed+" printed processed");
    }
    public String getRaw() {
        return raw;
    }
    public ArrayList<String> getProcessed() {
        return processed;
    }
    public void setTesting(boolean testing) {
        isTesting = testing;
    }
    public static boolean isDouble(char input) {
        return isDouble(String.valueOf(input));
    }
    public static boolean isDouble(String input) { 
        String input1 = new String(input);
        try {
            Double.parseDouble(input1); 
            return true;
        } 
        catch(Exception e) {  
            return false;
        } 
    }
    public static boolean isInteger(String input) { 
        int input1 = (int) Double.parseDouble(input);
        return input1 == Double.parseDouble(input);
    }
    public String getResult() {
        if (!isEvaluated) {
            String a = evaluate();
            if (isTesting) {
                System.out.println("ANSWER: "+origRaw+": "+a);
            }
            //System.out.println("ANSWER: "+origRaw+": "+a);
            return a;
        }
        if (isTesting) {
            System.out.println("ANSWER: "+origRaw+": "+evaluation);
        }
        return evaluation;
    }
    public String toString() {
        return raw;
    }
    public void setRaw(String s) {
        raw = s;
    }
    public void setProcessed(ArrayList<String> s) {
        processed = s;
    }
    private String toRaw() {
        String s = new String();
        for (int i=0;i<processed.size();i++) {
            s += processed.get(i);
            /*  if (i != processed.size()-1) {
                s += " ";
            }  */
        }
        throw(new Error("Invalid expression or error during calculation: "+origRaw));
        //return "ERROR: Invalid expression: "+origRaw;//+": "+processed;
    }
}
