class Operation {
    protected String definition;
    protected Dict<String,String> variables;
    protected Dict<String,Operation> functions;
    protected Dict<String,BiOperation> biFunctions;
    protected static final int defaultTaylorAccuracy = 8;
    private String var;
    public Operation() {
        this("");
    }
    public Operation(String definition) {
        this(definition,new Dict<String,String>(),new Dict<String,Operation>(), new Dict<String,BiOperation>());
        this.definition = definition;
    }
    public Operation(String definition, Dict<String,String> variables, Dict<String,Operation> functions, Dict<String,BiOperation> biFunctions) {
         this.definition = definition;
         this.variables = variables;
         this.functions = functions;
         this.biFunctions = biFunctions;
         this.var = "x";
    }
    public Operation(String definition, String var, Dict<String,String> variables, Dict<String,Operation> functions, Dict<String,BiOperation> biFunctions) {
         this.definition = definition;
         this.variables = variables;
         this.functions = functions;
         this.biFunctions = biFunctions;
         this.var = var;
    }
    public void setAllStored(Dict<String,String> variables, Dict<String,Operation> functions, Dict<String,BiOperation> biFunctions) {
        this.variables=variables;
        this.functions=functions;
        this.biFunctions=biFunctions;
    }
    public double evaluate(double n) {
        String evaluation = new String(definition);
        evaluation = evaluation.replace(var,","+n+",");
        Expression e = new Expression(evaluation,variables,functions,biFunctions);
        if (Expression.isDouble(e.getResult())) {
            return Double.parseDouble(e.getResult());
        } else {
            return 0.0/0.0;
        }
    }
    public Operation taylorSeries() {
        return taylorSeries(defaultTaylorAccuracy);
    }
    //fix so stackOverflow doesn't happen when 2 layers deep, second derivative
    public Operation taylorSeries(int taylorAccuracy) {
        double k = 0;
        Expression tempExp = new Expression();
        String s = new String();
        Operation o1 = this;
        String key = String.valueOf((char)8999);
        double o1Val;
        for (int i=0;i<taylorAccuracy;i++) {
            functions.add(key,o1);
            functions.replaceAll(key,o1);
            o1Val = o1.evaluate(k);
            // System.out.println(o1Val);
            if (o1Val != 0) {
                s += "("+o1Val+"x^"+i+")/"+tempExp.factorial(i)+" + ";
            }
            //set o1 to its own derivative
            o1 = new Operation(key+"'(x)",variables,functions,biFunctions);
            key = String.valueOf((char)(i+9000));
        }
        for (int i=-1;i<taylorAccuracy;i++) {
            functions.remove(String.valueOf((char)(i+9000)));
        }
        //if " + " are the last three characters, delete them
        if (s.length()>=3) {
            if (s.substring(s.length()-3).equals(" + ")) {
                s = s.substring(0,s.length()-3);
            }
        }
        Operation o = new Operation(s,variables,functions,biFunctions);
        return o;
    }
    public String derivative() {
        return new String();
    }
    public double secondDerivative(double n) {
        functions.add("f",this);
        Operation o1 = new Operation("f'y","y",variables,functions,biFunctions);
        functions.add("g",o1);
        //Operation o2 = new Operation("(g("+String.valueOf(n)+"+z)-g("+String.valueOf(n)+"))/z","z",variables,functions,biFunctions);
        Operation o2 = new Operation("g'z","z",variables,functions,biFunctions);
        functions.add("h",o2);

        Expression e = new Expression("h"+n,variables,functions,biFunctions);
        e.setLimitAccuracy(2);
        e.evaluate();
        e.setLimitAccuracy(5);
        functions.remove("f");
        functions.remove("g");
        functions.remove("h");
        if (Expression.isDouble(e.getResult())) {
            return Double.parseDouble(e.getResult());
        } else {
            return 0.0/0.0;
        }
    }
    public double derivative(double n) {
        //“() = this
        //‘() = “(n+x)-“(n))/x
        //lim x->0 ((f(n+x)-f(n))/x)
        functions.add("“",this);
        Operation o1 = new Operation("(“("+String.valueOf(n)+"+x)-“("+String.valueOf(n)+"))/x",variables,functions,biFunctions);
        functions.add("‘",o1);
        
        Expression e = new Expression("‘_(0)",variables,functions,biFunctions);
        e.evaluate();
        functions.remove("‘");
        functions.remove("“");
        if (Expression.isDouble(e.getResult())) {
            return Double.parseDouble(e.getResult());
        } else {
            return 0.0/0.0;
        }
    }
    public String toString() {
        return definition;
    }
}
