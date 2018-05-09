package generics.consumer;

public class GeneralistHerbivore implements Consumer<Vegetables> {

	@Override
	public void consume(Vegetables a) {
		if(a instanceof Bamboo){
			System.out.println("That's bamboo! Burn, bamboo!");
		}else if(a instanceof Vegetables){
			System.out.println("Water evaporates, vegetable burns.");
		}
	}

}
