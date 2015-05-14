public class StepStack {
	private static StepStack instance;
	private GUI gui;
	private Step[] steps;
	private int size;
	private int stepCounter;
	private int stackPointer;
	private StepStack(int size) {
		stepCounter = 0;
		stackPointer = 0;
		steps = new Step[size];
		this.size = size;
	}
	public static synchronized StepStack getInstance () {
		if (StepStack.instance == null) {
			StepStack.instance = new StepStack (100);// <- hier Größe ändern
			instance.gui = GUI.getInstance();
	    }
	    return StepStack.instance;
	}
	
	public void resetStepStack() {
		stepCounter = 0;
		stackPointer = 0;
	}
	
	
	public void pushStack(Step newStep) {
		normalizePointer();
		steps[stackPointer] = newStep;
		stackPointer++;
		if(stepCounter < size) {
			stepCounter++;
		}
		System.out.println("stack wurde gepushed");
		System.out.println("stepctr: " + stepCounter);
		System.out.println("stackptr: " + stackPointer);
		System.out.println("step: " + steps[stackPointer-1].pc);
		gui.btnUndo.setEnabled(true);
	}
	
	public Step popStack() {
		if(stepCounter<=0) {
			System.out.println("RETURN NULL");
			return null;
		}
		stepCounter--;
		stackPointer--;
		normalizePointer();
		System.out.println("stack wurde gepushed");
		System.out.println("stepctr: " + stepCounter);
		System.out.println("stackptr: " + stackPointer);
		System.out.println("step: " + steps[stackPointer].pc);
		return steps[stackPointer];
	}
	
	private void normalizePointer() {
		while(stackPointer >= size){
			stackPointer -= size;
		}
		while(stackPointer < 0) {
			stackPointer += size;
		}
	}
	
}

class Step{
	storage sto = storage.getInstance();
	 int[] dataStorage = new int[256]; // Array für 8 bit Datenspeicher
	 int pc = 0;
	 int[] stack = new int[8];
	 int stackptr = 0;
	 int w = 0;
	 double time = 0;
	 double deltatime = 1;
	 int konstDelta = 25;
	 double externalClock = 1000; //alle x usec wird der externe clock ausgelöst
	//externe frequenz ist 1/externalClock(10)^-6
	 double externalClockCount;
	 boolean clockIsRunning = false;

	
	Step() {
		for(int i=0;i<256;i++){
		this.dataStorage[i] = sto.dataStorage[i];
		}
		this.pc = sto.pc;
		for(int i=0;i<8;i++){
			this.stack[i] = sto.stack[i];
		}
		this.stackptr = sto.stackptr;
		this.w = sto.w;
		this.time = sto.time;
		this.deltatime = sto.deltatime;
		this.konstDelta = sto.konstDelta;
		this.externalClock = sto.externalClock;
		this.externalClockCount = sto.externalClockCount;
		this.clockIsRunning = sto.clockIsRunning;
	}
}
