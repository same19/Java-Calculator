class BiOperation extends Operation {
    private double defaultY;
    public BiOperation() {
        this("x",0);
    }
    public BiOperation(String definition) {
        this(definition,0);
    }
    public BiOperation(String definition, double defaultY) {
        this(definition,defaultY,new Dict<String,String>(),new Dict<String,Operation>(), new Dict<String,BiOperation>());
    }
    public BiOperation(String definition, double defaultY, Dict<String,String> variables, Dict<String,Operation> functions, Dict<String,BiOperation> biFunctions) {
        super (definition,variables,functions,biFunctions);
        this.defaultY = defaultY;
    }
    public double evaluate(double x, double y) {
        String evaluation = new String(definition);
        variables.add("x",String.valueOf(x));
        variables.replaceAll("x",String.valueOf(x));
        variables.add("y",String.valueOf(y));
        variables.replaceAll("y",String.valueOf(y));
        Expression e = new Expression(evaluation,variables,functions,biFunctions);
        variables.remove("x");
        variables.remove("y");
        if (Expression.isDouble(e.getResult())) {
            return Double.parseDouble(e.getResult());
        } else {
            return 0.0/0.0;
        }
    }
    public double evaluate(double n) {
        String evaluation = new String(definition);
        variables.add("x",String.valueOf(n));
        variables.replaceAll("x",String.valueOf(n));
        variables.add("y",String.valueOf(defaultY));
        variables.replaceAll("y",String.valueOf(defaultY));
        Expression e = new Expression(evaluation,variables,functions,biFunctions);
        variables.remove("x");
        variables.remove("y");
        if (Expression.isDouble(e.getResult())) {
            return Double.parseDouble(e.getResult());
        } else {
            return 0.0/0.0;
        }
    }
}
