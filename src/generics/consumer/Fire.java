package generics.consumer;

public class Fire implements Consumer<EnergySource> {

	@Override
	public void consume(EnergySource a) {
		if(a instanceof Bamboo){
			System.out.println("That's bamboo! Burn, bamboo!");
		}else if(a instanceof Vegetables){
			System.out.println("Water evaporates, vegetable burns.");
		}else if(a instanceof EnergySource){
			System.out.println("A generic energy source. It burns.");
		}
	}

}
