package model;

public enum ProbabilityCostumerEntry {
	night(0.05), morning(0.20), early_afternoon(0.10), late_afternoon(0.15), evening(0.20);
	
	private double probability;
	
	private ProbabilityCostumerEntry(final double probability) {this.probability = probability;}
	
	public double getValue(){
		return probability;
	}
}
