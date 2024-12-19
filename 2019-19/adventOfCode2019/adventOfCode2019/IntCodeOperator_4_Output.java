package adventOfCode2019;

public class IntCodeOperator_4_Output implements IntCodeOperator {

	@Override
	public OperationMode getOperationMode() 
	{
		return OperationMode.valueOfOperation(4);
	}

	@Override
	public void operate(IntCode intCode, OpCode opCode)
	{
		//logLastDiagnosticIfNonZeroOutput(intCode);
		
		long diagnostics = intCode.getValueForFirstParameterAccordingToParameterMode(opCode);
		intCode.addToDiagnosticOutput(diagnostics);
		
		String logging = "Output " + diagnostics;
		
		if (intCode.getShouldPauseOnOutput())
		{
			logging += " and paused";
			intCode.setPaused(true);
		}
		
		intCode.logIfDebugIntCode(logging);
		
		updateInstructionPointer(intCode);
	}

	/*private void logLastDiagnosticIfNonZeroOutput(IntCode intCode)
	{
		Vector<Long> diagnosticOutput = intCode.getDiagnosticOutput(true);
		if (!diagnosticOutput.isEmpty())
		{
			Long lastDiagnosticOutput = diagnosticOutput.lastElement();
			Long lastElement = lastDiagnosticOutput;
			if (lastElement != 0)
			{
				System.out.println(lastDiagnosticOutput + " at index " + intCode.getInstructionPointer());
			}
		}
	}*/

	@Override
	public void updateInstructionPointer(IntCode intCode)
	{
		intCode.increaseInstructionPointer(2);
		intCode.setUpdatedInstructionPointer(true);
	}

}
