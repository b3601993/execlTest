package generics;

import generics.consumer.Bamboo;
import generics.consumer.EnergySource;
import generics.consumer.Fire;
import generics.consumer.GeneralistHerbivore;
import generics.consumer.Panda;
import generics.consumer.Vegetables;

public class GenericsTest {

	public static void main(String[] args) {
		EnergySource e =new EnergySource();
		Vegetables v = new Vegetables();
		Bamboo b = new Bamboo();
		
		Fire f = new Fire();
		f.consume(e);
		f.consume(v);
		f.consume(b);
		
		GeneralistHerbivore g = new GeneralistHerbivore();
		g.consume(b);
		g.consume(v);
//		g.consume(e);
		
		Panda p = new Panda();
		p.consume(b);
//		p.consume(v);
//		p.consume(e);
	}
}
