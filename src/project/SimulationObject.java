package project;

public abstract class SimulationObject {

	private String token;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	@Override
	public String toString(){
		return this.token;
	}
}
