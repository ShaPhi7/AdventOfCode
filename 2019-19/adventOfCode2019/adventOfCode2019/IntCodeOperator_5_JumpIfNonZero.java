package adventOfCode2019;

public class IntCodeOperator_5_JumpIfNonZero implements IntCodeOperator 
{

	@Override
	public OperationMode getOperationMode() {
		return OperationMode.valueOfOperation(5);
	}

	@Override
	public void operate(IntCode intCode, OpCode opCode) {
		intCode.processInstructionForOpCode5or6(opCode);
	}

	@Override
	public void updateInstructionPointer(IntCode intCode) {
		//done during processing for simplicity.
	}

}