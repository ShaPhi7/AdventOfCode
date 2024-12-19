package adventOfCode2019;

public abstract interface IntCodeOperator
{	
	public OperationMode getOperationMode();
	public void operate(IntCode intCode, OpCode opCode); 
	public void updateInstructionPointer(IntCode intCode);
}