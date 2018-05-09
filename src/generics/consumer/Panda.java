package generics.consumer;

public class Panda implements Consumer<Bamboo> {

	@Override
	public void consume(Bamboo a) {
		if(a instanceof Bamboo){
			System.out.println("That's bamboo! Burn, bamboo!");
		}
	}

}
