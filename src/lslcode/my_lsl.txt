libsl "1.0.0";
library com.pepyachka;

typealias Work = Work;
typealias ArrayArrayWork = array<array<work>>;
typealias String = String;
typealias Person = Person;
typealias ArrayWork = array<work>;
typealias Employee = Employee;
typealias int = int;

automaton Person : Person {
	initstate Created;

	fun getName(): String;
	fun getL(arg0: String): String;
	fun setName(arg0: String): void;
}

automaton Work : Work {
	initstate Created;

	fun getName(): String;
}