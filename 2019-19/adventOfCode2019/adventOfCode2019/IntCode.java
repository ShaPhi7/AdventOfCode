package adventOfCode2019;

import java.util.List;
import java.util.Vector;

/*
 * Some awful code I wrote ages ago!
 */
public class IntCode {
	
	private boolean debugIntCode = false;
	
	private static final int ADDRESS_NOUN = 1;
	private static final int ADDRESS_VERB = 2;

	private static final int NUMBER_OF_ZEROES_TO_PAD_INT_CODES = 10000;
	
	private Vector<Long> intCodes = new Vector<Long>(10000); //make this list iterator?
	private int instructionPointer = 0;
	private Vector<Long> initialState = new Vector<Long>();
	private boolean processed = false;
	private boolean paused = false;
	private Vector<Long> userInput = new Vector<Long>();
	//private ListIterator<Long> userInputListIterator = userInput.listIterator();
	private int nextUserInputIndex = 0;
	private Vector<Long> diagnosticOutput = new Vector<Long>();
	private Vector<Long> diagnosticOutputsSinceLastInput = new Vector<Long>();
	private boolean updatedInstructionPointer = false;
	private boolean shouldPauseOnOutput = false;
	private int relativeBase = 0;
	private int cycleCount = 1;

	public int getRelativeBase() {
		return relativeBase;
	}

	public void setRelativeBase(int relativeBase) {
		this.relativeBase = relativeBase;
	}
	
	public void offsetRelativeBase(int offset)
	{
		int oldRelativeBase = getRelativeBase();
		setRelativeBase(oldRelativeBase + offset);
	}

	public Long getNextUserInputOrPauseIfNone()
	{
		Long Long = null;
		if (nextUserInputIndex < userInput.size())
		{
			Long = userInput.get(nextUserInputIndex);
			nextUserInputIndex++;
		}
		else
		{
			logIfDebugIntCode("Pausing as no user input");
			paused = true;
		}
		return Long;
	}
	
	public Vector<Long> getOutputsSinceLastInput()
	{
		if (!processed)
		{
			process();
		}
		
		return diagnosticOutputsSinceLastInput;
	}
	
	public long getOutputLastDiagnosticStyle()
	{
		return getDiagnosticOutput(false).lastElement();
	}
	
	public Vector<Long> getDiagnosticOutput(boolean allowUnprocessedRead)
	{
		if (!processed
		  && !allowUnprocessedRead)
		{
			process();
		}
		
		return diagnosticOutput;
	}

	public void setDiagnosticOutput(Vector<Long> diagnosticOutput) {
		this.diagnosticOutput = diagnosticOutput;
	}
	
	public void addToDiagnosticOutput(long diagnostics)
	{
		diagnosticOutput.add(diagnostics);
		diagnosticOutputsSinceLastInput.add(diagnostics);
	}

	public Vector<Long> getUserInput() {
		return userInput;
	}

	public void setUserInput(Vector<Long> userInput) {
		this.userInput = userInput;
	}
	
	public void addToUserInput(long userInput)
	{
		this.userInput.add(userInput);
	}

	public IntCode(Vector<Long> initialState)
	{
		this.initialState = new Vector<Long>(initialState);
	}
	
	public IntCode(List<Long> initialState)
	{
		Vector<Long> vInitialState = new Vector<Long>(initialState);
		this.initialState = new Vector<Long>(vInitialState);
	}
	
	public IntCode(Vector<Long> initialState, boolean shouldPauseOnOutput)
	{
		this.initialState = new Vector<Long>(initialState);
		this.shouldPauseOnOutput = shouldPauseOnOutput;
	}
	
	public Vector<Long> getIntCodes()
	{
		return intCodes;
	}
	
	public Vector<Long> getInitialState()
	{
		return initialState;
	}
	
	public void changeFirstElementOfInitialState(long newIndex)
	{
		getInitialState().setElementAt(newIndex, 0);
	}
	
	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean getShouldPauseOnOutput() {
		return shouldPauseOnOutput;
	}

	public Long getOutputFirstIntCodeStyle()
	{
		if (!processed)
		{
			process();
		}
		return intCodes.firstElement();
	}
	
	public Long getNoun()
	{
		if (!processed)
		{
			process();
		}
		return intCodes.get(ADDRESS_NOUN);
	}
	
	public Long getVerb()
	{
		if (!processed)
		{
			process();
		}
		return intCodes.get(ADDRESS_VERB);
	}
	
	public Long getValueAtAddress(int address)
	{
		if (!processed)
		{
			process();
		}
		return intCodes.get(address);
	}
	
	private void process()
	{
		if (!prepareToProcess())
		{
			return;
		}
		
		while (!processed
		  && !paused)
		{
			resetUpdatedInstructionPointer();
			
			OpCode opCode = new OpCode(intCodes.get(instructionPointer), instructionPointer);
			IntCodeOperator operator = getIntCodeOperator(opCode);

			operator.operate(this, opCode);
			
			checkUpdatedInstructionPointer(opCode);
			
			setCycleCount(getCycleCount() + 1);
		}
	}

	private boolean prepareToProcess() {
		if (!paused)
		{
			System.out.println("Starting processing");
			
			if (!prepareToStartOrRestartProcessing())
			{
				return false;
			}
		}
		else
		{
			System.out.println("Resuming processing");
			
			if (!prepareToResumeProcessing())
			{
				return false;
			}
		}
		
		return true;
	}

	/*
	 * deals with the case where new inputs have been added after we'd been waiting for them.
	 */
	private boolean prepareToResumeProcessing() {
		//listIterators get updated automatically when their list is changed.
		
		//don't need here, will pause immediately if takes an input and doesn't have one.
		/*if (nextUserInputIndex >= userInput.size())
		{
			System.out.println("Still not got an input so remaining paused.");
			return false;
		}*/
		
		diagnosticOutputsSinceLastInput = new Vector<Long>();
		paused = false;
		
		return true;
	}

	private boolean prepareToStartOrRestartProcessing() {
		if (!validToProcess())
		{
			System.err.println("Did not process invalid IntCode.");
			return false;
		}
		
		resetVariablesResultingFromProcessing();
		
		return true;
	}

	public void resetVariablesResultingFromProcessing() {
		intCodes = new Vector<Long>(initialState);
		for(int i=0; i<NUMBER_OF_ZEROES_TO_PAD_INT_CODES;i++)
		{
			intCodes.add(0l);
		}
		
		diagnosticOutput = new Vector<Long>();
		diagnosticOutputsSinceLastInput = new Vector<Long>();
		nextUserInputIndex = 0;
		setInstructionPointer(0);
		paused = false;
		processed = false;
		relativeBase = 0;
		cycleCount = 1;
	}

	public long getInstructionPointer() 
	{
		return instructionPointer;
	}

	public void setInstructionPointer(int indexToSetInstructionPointer) 
	{
		this.instructionPointer = indexToSetInstructionPointer;
	}
	
	public void increaseInstructionPointer(int amountToIncreaseBy) 
	{
		instructionPointer += amountToIncreaseBy;
	}
	
	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	private IntCodeOperator getIntCodeOperator(OpCode opCode) {
		
		switch (opCode.getOperationMode())
		{
		case ADDITION:
			return new IntCodeOperator_1_Addition();
		case MULTIPLICATION:
			return new IntCodeOperator_2_Multiplication();
		case INPUT:
			return new IntCodeOperator_3_Input();
		case OUTPUT:
			return new IntCodeOperator_4_Output();
		case JUMP_IF_NONZERO:
			return new IntCodeOperator_5_JumpIfNonZero();
		case JUMP_IF_ZERO:
			return new IntCodeOperator_6_JumpIfZero();
		case LESS_THAN:
			return new IntCodeOperator_7_LessThan();
		case EQUAL_TO:
			return new IntCodeOperator_8_EqualTo();
		case RELATIVE_BASE_OFFSET:
			return new IntCodeOperator_9_RelativeBaseOffset();
		case TERMINATION:
			return new IntCodeOperator_99_Termination();
		default:
		System.err.println("Something went wrong. Encountered opCode " + opCode.getOperationModeOperation() + " at index " + instructionPointer);
			return null;
		}
	}

	/*private void processInputOperationMode(int userInput)
	{	
		int indexToOverwriteWithUserInput = intCodes.get(instructionPointer+1);
		intCodes.set(indexToOverwriteWithUserInput, userInput);
	}*/

	private boolean validToProcess() {
		if (processed == true)
		{
			System.err.println("This intCode has been processed already. Not processing.");
			return false;
		}
		
		if (initialState.isEmpty())
		{
			System.err.println("No initial state memory.");
			return false;
		}
		
		return true;
	}

	public void processOpCodesWithThreeParms(OpCode opCode) {
		
		long firstValue = getValueForFirstParameterAccordingToParameterMode(opCode);
		long secondValue = getValueForSecondParameterAccordingToParameterMode(opCode);
		int indexToOverwriteWithNewValue = getIndexForThirdParameterAccordingToParameterMode(opCode);
		//int indexToOverwriteWithNewValue = intCodes.get(instructionPointer+3).intValue();
		long newValue = calculateNewValueForOpCodesWithThreeParms(opCode, firstValue, secondValue);

		System.out.println("setting index " + indexToOverwriteWithNewValue + " to " 
				+ newValue + " as opCode is " + opCode.getOperationMode() + " for parameters " + opCode.getParameterModes());
		
		intCodes.set(indexToOverwriteWithNewValue, newValue);
	}

	private long calculateNewValueForOpCodesWithThreeParms(OpCode opCode, long firstValue, long secondValue) 
	{
		long newValue = -1;
		switch (OperationMode.valueOfOperation(opCode.getOperationModeOperation()))
		{
			case ADDITION:
				newValue = firstValue + secondValue;//and this? and the others?
				break;
			case MULTIPLICATION:
				newValue = firstValue * secondValue;//is this cutting a long down?
				break;
			case LESS_THAN:
				newValue = firstValue < secondValue? 1 : 0;
				break;
			case EQUAL_TO:
				newValue = firstValue == secondValue? 1 : 0;
				break;
			default: System.err.println("Do we have breaks? No? AHHHH!!! PANIC!");
		}
		return newValue;
	}
	
	public void processInstructionForOpCode5or6(OpCode opCode)
	{
		long firstValue = getValueForFirstParameterAccordingToParameterMode(opCode);
		long newValueForInstructionPointer = getValueForSecondParameterAccordingToParameterMode(opCode);
		
		switch (OperationMode.valueOfOperation(opCode.getOperationModeOperation()))
		{
		case JUMP_IF_NONZERO:
			if (firstValue != 0)
			{
				Long indexToSetInstructionPointerLong = Long.valueOf(newValueForInstructionPointer);
				
				System.out.println("setting instruction pointer to " + newValueForInstructionPointer 
						+ " as opCode is " + opCode.getOperationMode() + " for parameters " + opCode.getParameterModes());
				
				setInstructionPointer(indexToSetInstructionPointerLong.intValue());
			}
			else
			{
				System.out.println("not jumping instruction pointer" 
						+ " as opCode is " + opCode.getOperationMode() + " for parameters " + opCode.getParameterModes());
					
				increaseInstructionPointer(3);
			}
			break;
		case JUMP_IF_ZERO:
			if (firstValue == 0)
			{
				System.out.println("setting instruction pointer to " + newValueForInstructionPointer 
						+ " as opCode is " + opCode.getOperationMode() + " for parameters " + opCode.getParameterModes());
				
				Long indexToSetInstructionPointerLong = Long.valueOf(newValueForInstructionPointer);
				instructionPointer = indexToSetInstructionPointerLong.intValue();
			}
			else {
				System.out.println("not jumping instruction pointer" 
						+ " as opCode is " + opCode.getOperationMode() + " for parameters " + opCode.getParameterModes());
				
				increaseInstructionPointer(3);
			}
			break;
		default:
			System.err.println("Do we have breaks? No? OHHHH!!! PANIC!");
			break;
		}
		
		setUpdatedInstructionPointer(true);
	}
	
	public long getValueForFirstParameterAccordingToParameterMode(OpCode opCode)
	{
		return getValueForParameterAccordingToParameterMode(opCode, 0, instructionPointer+1);
	}
	
	public long getValueForSecondParameterAccordingToParameterMode(OpCode opCode)
	{
		return getValueForParameterAccordingToParameterMode(opCode, 1, instructionPointer+2);
	}
	
	public long getValueForThirdParameterAccordingToParameterMode(OpCode opCode)
	{
		return getValueForParameterAccordingToParameterMode(opCode, 2, instructionPointer+3);
	}
	
	public long getValueForParameterAccordingToParameterMode(OpCode opCode, int parameterIndex, int intCodeIndex) {
		
		long value = -1;
		ParameterMode parameterMode = opCode.getParameterMode(parameterIndex);
		switch (parameterMode)
		{
		case POSITION:
			int positionToReadValue = intCodes.get(intCodeIndex).intValue();
			value = intCodes.get(positionToReadValue);
			break;
		case IMMEDIATE:
			value = intCodes.get(intCodeIndex);
			break;
		case RELATIVE:
			int relativeOffset = intCodes.get(intCodeIndex).intValue();
			value = intCodes.get(relativeOffset + relativeBase);
			break;
		}
		
		return value;
	}
	
	public int getIndexForFirstParameterAccordingToParameterMode(OpCode opCode)
	{
		return getIndexForParameterAccordingToParameterMode(opCode, 0, instructionPointer+1);
	}
	
	public int getIndexForSecondParameterAccordingToParameterMode(OpCode opCode)
	{
		return getIndexForParameterAccordingToParameterMode(opCode, 1, instructionPointer+2);
	}
	
	public int getIndexForThirdParameterAccordingToParameterMode(OpCode opCode)
	{
		return getIndexForParameterAccordingToParameterMode(opCode, 2, instructionPointer+3);
	}
	
	public int getIndexForParameterAccordingToParameterMode(OpCode opCode, int parameterIndex, int intCodeIndex)
	{
		int indexToReturn = -1;
		ParameterMode parameterMode = opCode.getParameterMode(parameterIndex);
		switch (parameterMode)
		{
		case POSITION:
			indexToReturn = intCodes.get(intCodeIndex).intValue();
			break;
		case IMMEDIATE:
			System.out.println("Didn't think you needed Immediate mode for index");
			break;
		case RELATIVE:
			int relativeOffset = intCodes.get(intCodeIndex).intValue();
			indexToReturn = relativeOffset + relativeBase;
			break;
		}
		
		return indexToReturn;
	}
	public void doNounVerbSubstitution(long noun, long verb)
	{
		initialState.set(ADDRESS_NOUN, noun);
		initialState.set(ADDRESS_VERB, verb);
	}

	public boolean getUpdatedInstructionPointer() {
		return updatedInstructionPointer;
	}

	public void setUpdatedInstructionPointer(boolean updatedInstructionPointer) {
		this.updatedInstructionPointer = updatedInstructionPointer;
	}
	
	public void resetUpdatedInstructionPointer()
	{
		setUpdatedInstructionPointer(false);		
	}
	
	public void checkUpdatedInstructionPointer(OpCode opCode) 
	{
		if (!paused
		  || opCode.getOperationMode() == OperationMode.OUTPUT)
		{
			if (!updatedInstructionPointer)
			{
				System.out.println("You're not updating the instruction pointer properly.");
			}
		}
		else
		{
			if (updatedInstructionPointer)
			{
				System.out.println("You've updated the instruction pointer when you should be pausing.");
			}
		}
		
	}

	public int getCycleCount() {
		return cycleCount;
	}

	public void setCycleCount(int cycleCount) {
		this.cycleCount = cycleCount;
	}
	
	public void logIfDebugIntCode(String logging)
	{
		if (!debugIntCode)
		{
			return;
		}
		
		StackTraceElement[] ste = Thread.currentThread().getStackTrace(); 
		
		String msg = "Cycle " + getCycleCount() + ", "
					+ "index " + getInstructionPointer() + ", "
					+ "opCode " + intCodes.get(instructionPointer) + ": " 
					+ logging;
		
		System.out.println(msg);
		System.out.println(ste[2].getClassName() + "." + ste[2].getMethodName() + "()");
	}
}