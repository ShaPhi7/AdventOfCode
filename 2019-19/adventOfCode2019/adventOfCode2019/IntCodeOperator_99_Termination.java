package adventOfCode2019;

public class IntCodeOperator_99_Termination implements IntCodeOperator {

	@Override
	public OperationMode getOperationMode() 
	{
		OperationMode.valueOfOperation(99);
		return null;
	}

	@Override
	public void operate(IntCode intCode, OpCode opCode) 
	{
		intCode.setProcessed(true);
		
		intCode.logIfDebugIntCode("Terminating");
		
		updateInstructionPointer(intCode);
	}

	@Override
	public void updateInstructionPointer(IntCode intCode) 
	{
		intCode.increaseInstructionPointer(0);
		intCode.setUpdatedInstructionPointer(true);
	}

}
