package w.spring.extras.generator.model;

public class GenericHolder<M extends Marker> {

	private M holding;
	
	private Class<? extends M> clazz;

	public M getHolding() {return holding;}
	public void setHolding(M holding) {this.holding = holding;}

	public Class<? extends M> getClazz() {return clazz;}
	public void setClazz(Class<? extends M> clazz) {this.clazz = clazz;}

	@Override
	public String toString() {
		return "Holder [holding=" + holding + ", clazz=" + clazz + "]";
	}
}
