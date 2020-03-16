import java.util.ArrayList;
import java.util.Date;

public class Person {
	String name;
	Double risk;
	ArrayList<Person>contacts;
	ArrayList<Interaction>interactions;
	
	public Person(String name) {
		this.name=name;
		this.risk=0.0;
		this.interactions=new ArrayList<Interaction>();
		this.contacts=new ArrayList<Person>();
	}
	
	public boolean reportInteraction(ArrayList<Person> persons,int volume, int duration, byte type,Date when) {
		Interaction i=new Interaction(this, persons,volume, duration, type,when);
		for (Person p :persons) {
			if (!contacts.contains(p)) {
				contacts.add(p);
			}
		}
		return true;
	}
	
	public Double getRisk() {
		return risk;
	}
	
	public boolean setRisk(Double newRisk) {
		this.risk=newRisk;
		return true;
	}
	
}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
public class Interaction {
	
	ArrayList<Person>persons;
	int volume;
	int duration;
	int numberOfPeople;
	int height;
	int length;
	int width;
	public Date when;
	boolean totalRisk;
	Person initiator;
	Double confirmed;
	Double distance;
	boolean indoor;
	HashMap<Person,Double> risks;
	/*
	 * Type of interaction can be:
	 * 		1 - Indoor non-residential space - office, conference room, elevator, plane, bus, train compartment, bar, etc
	 * 		2 - Residential space - someone's living quarters)
	 * 		3 - Outdoor/very large venue - Airport, railway station, mall, supermarket, restaurant, hotels
	*/		
	byte type;
	//status to keep track of which participants confirmed the interaction
	HashMap<Person,Boolean> status;
	public Interaction(Person initiator,ArrayList<Person> persons,int volume,int duration,byte type,Date when) {
		risks=new HashMap<Person,Double>();
		this.persons=persons;
		this.type=type;
		this.when=when;
		if (!persons.contains(initiator)){persons.add(initiator);}
		System.out.println("========Creating interaction between "+persons.size()+" people.");
		this.duration=duration;
		this.initiator=initiator;
		this.status=new HashMap<Person,Boolean>();
		this.distance=(double) (volume/(persons.size()*duration));
		if (distance<10) {distance=10.0;}
		this.distance=normalize(distance);
		System.out.println("");
		//if interaction is at maximum risk of transmission
		if (this.distance==0.0) {
			double totalNonRisk=1;
			for (Person p : persons) {
				double nonrisk=1-p.getRisk();
				totalNonRisk=totalNonRisk*nonrisk;
			}
			for (Person p:persons) {
				p.risk=1-totalNonRisk;
				double risk=p.risk;
				risks.put(p, risk);
			}
		//if interaction is not 100 percent likely to transmit viruses
		}else{
			for (Person p :persons){
				double totalNonRisk=1;
				for (Person m :persons) {
					if (p.equals(m)){
						continue;
					}
					double nonrisk=(1-m.getRisk()*distance);
					totalNonRisk=totalNonRisk*(nonrisk);	
				}
				p.risk=1-totalNonRisk*(1-p.risk);
				double risk=p.risk;
				risks.put(p, risk);
			}
		}
		for (Person p:persons) {p.interactions.add(this);}
		Engine.update(this);
	}
	
	void update(Person person) {
		if (risks.get(person)!=person.risk) {
			for (Person p:this.persons) {
				if (p==person) {continue;}
				p.risk=1-(1-p.risk)*(1-person.risk);
				Engine.update(this);
			}
		}else {return;}
	}
	
	public static double normalize(double value) {
		double max=100;double min=10;
		double result1=((value-min))/(max-min);return result1;
	}
	
}

import java.util.ArrayList;
import java.util.Date;

public class Engine {
	public static ArrayList<Person>people=new ArrayList<Person>();
	public static void main(String[] args) {
		
		System.out.println("Hello");
		Person sam=new Person("Sam");Person jim=new Person("Jim");Person tim=new Person("Tim");Person al=new Person("Al");
		Person gus=new Person("Gus");Person amy=new Person("Amy");Person joe=new Person("Joe");Person liz=new Person("Liz");
		Person sal=new Person("Sal");Person ed=new Person("Ed");Person lou=new Person("Lou");
		
	people.add(sam);people.add(jim);people.add(tim);people.add(al);people.add(gus);people.add(amy);people.add(joe);people.add(liz);
	people.add(sal);people.add(ed);people.add(lou);
	
		sam.risk=0.05;jim.risk=0.1;tim.risk=0.7;al.risk=0.03;gus.risk=0.09;amy.risk=0.0;joe.risk=0.004;liz.risk=0.3;
		sal.risk=0.08;ed.risk=0.003;lou.risk=0.12;
		
		ArrayList<Person>samMet=new ArrayList<Person>();
		samMet.add(jim);
		samMet.add(tim);
		for (Person p : people) {System.out.println(p.name +" exposure risk "+ p.risk);}
		sam.reportInteraction(samMet, 400,10, (byte) 1, new Date());
		ArrayList<Person>timMet=new ArrayList<Person>();timMet.add(amy);
		
		tim.reportInteraction(timMet, 200, 3, (byte)1, new Date());
	}
	
	//Update function uses the Interaction constructor for recursiveness. 
	//This should propagate all throughout the nodes at every risk update.
	
	public static void update(Interaction i) {
		int count=0;
		System.out.println("Am intrat");
		for (Person p : i.persons) {
			for (Interaction j:p.interactions) {
				System.out.println("al doilea for");
				if (!i.equals(j)){
					System.out.println("si aici");
					for (Person m:j.persons) {
						
						count++;
						if (count==10) {return;}
						j.update(m);
					}
				}
			}
		}
	
	for (Person p : people) {System.out.println(p.name +" exposure risk "+ p.risk);}
	}
	
}

