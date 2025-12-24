package main.java.evaluator;

public class EvaluationResult {
    private final String prompt;
    private final String modelResponse;
    private final String label;
    private final String reason;
    public EvaluationResult(String prompt, String modelResponse, String label, String reason){
        this.label=label;
        this.reason=reason;
        this.modelResponse=modelResponse;
        this.prompt=prompt;
    }
    public String getLabel(){return label;}
    public String getResponse(){return reason;}

    @Override
    public String toString(){
        return "Result of evaluation :" + label + "|" + reason;
    }
}
