

public class StaticTest {

	String name;
	static int s; //statische test Variabel
	int notS; // Klassenvariabel
	
	/**
		Konstruktor
	*/
	public StaticTest (String name) {
		this.name=name;	
	}

	/**
		"normale" Methode
	*/
	void incNotS () {
		notS++;	
	}
	/*
	// funktioniert nicht, da die Methode incNotS2 statisch ist,
	// und sich daher nicht auf eine spezifische Instanz der
	// Klasse bezieht, sondern auf die Klasse an sich.
	// Da die Variabel "notS" aber nicht statisch ist, bezieht sicht
	// "notS" auf eine spezifische Instanz.  
	
	static void incNotS2 () {
		notS++;	
	}
	*/
	
	/**
		statische Methode, um statische Variabel hochzuzaehlen
	*/
	static void incS () {
		s++;	
	}

	/*
		Das wiederrum funktioniert, obwohl "s" statisch ist
		und incS2 nicht. Instanzen koennen auf Ihre Klasse, in der 
		die statischen Daten definiert sind zurueckgreifen, aber 
		nicht umgekehrt.
	*/
	void incS2 (){
		s++;	
	}
	/**
		"normale" Methode, die eine statische Methode aufruft.
	*/
	void nochmal () {
		incS (); // man kann auch aus einer
			// nichtstatischen Methode eine
			// statische Aufrufen
	}
	
	static void kann_das_funktionieren () {
		nochmal ();
		// Bonusfrage: kann man aus eine statischen Methode eine
		// nichtstatische Methode aufrufen?
	}	
	
	/**
		Zustand Anzeigen.
	*/
	void dump () {
		System.out.println (this.name);
		System.out.println (s);
		System.out.println (notS);
	}

	public static void main (String [] args) {
		StaticTest test1 = new StaticTest ("eins");
		StaticTest test2 = new StaticTest ("zwei");

		test1.incNotS ();
		test1.incS ();
		test1.incS2 ();

		test1.dump(); // Ergebnis?
		test2.dump();

		test2.incS2 ();
		
		test1.dump(); // Ergebnis ?
		test2.dump();

		StaticTest.incS();
		
		test1.dump(); // Ergebnis ?
		test2.dump();

	}
}
