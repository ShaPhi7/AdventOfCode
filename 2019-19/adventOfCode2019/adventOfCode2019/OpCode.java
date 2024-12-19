package adventOfCode2019;

import java.util.Vector;

public class OpCode {

	private long operationModeOperation = -1;
	private Vector<Integer> parameterModes = new Vector<Integer>();
	private int intCodeIndex = -1; //current position in the intCode that it belongs to.
	
	public int getIntCodeIndex() {
		return intCodeIndex;
	}

	public void setIntCodeIndex(int intCodeIndex) {
		this.intCodeIndex = intCodeIndex;
	}

	static int DEFAULT_PARAMETER_CODE = 0;
	
	public ParameterMode getParameterMode(int index)
	{
		int parameterMode = getParameterModeInt(index);
		return ParameterMode.valueOfOperation(parameterMode);
	}
	
	public int getParameterModeInt(int index)
	{
		if (index < getParameterModes().size())
		{
			return getParameterModes().get(index);
		}

		return DEFAULT_PARAMETER_CODE;
		
	}
	
	public Vector<Integer> getParameterModes() {
		return parameterModes;
	}

	public void setParameterModes(Vector<Integer> parameterModes) {
		this.parameterModes = parameterModes;
	}
	
	public OperationMode getOperationMode()
	{
		return OperationMode.valueOfOperation(operationModeOperation);
	}
	
	public long getOperationModeOperation() {
		return operationModeOperation;
	}

	public void setOperationModeOperation(int operationModeOperation) {
		this.operationModeOperation = operationModeOperation;
	}

	public OpCode(long operationModeAndParameterModes, int intCodeIndex)
	{
		String operationModeAndParamterModesStr = String.valueOf(operationModeAndParameterModes);
		
		if (operationModeAndParamterModesStr.length() < 3)
		{
			operationModeOperation = operationModeAndParameterModes;
		}
		else 
		{
			readOperationMode(operationModeAndParamterModesStr);
			readParameterModes(operationModeAndParamterModesStr);
		}
		
		this.intCodeIndex = intCodeIndex;
	}

	private void readParameterModes(String operationModeAndParamterModesStr) {

		if (operationModeAndParamterModesStr.length() > 2)
		{
			for (int i=operationModeAndParamterModesStr.length()-3; i>-1; i--)
			{
				char parmMode = operationModeAndParamterModesStr.charAt(i);
				String parmModeStr = String.valueOf(parmMode); 
				parameterModes.add(Integer.valueOf(parmModeStr));
			}
		}
	}

	private void readOperationMode(String operationModeAndParamterModesStr)
	{
		if (operationModeAndParamterModesStr.length() > 2)
		{
			String operationModeStr = operationModeAndParamterModesStr.substring(operationModeAndParamterModesStr.length()-2, operationModeAndParamterModesStr.length());
			setOperationModeOperation(Integer.valueOf(operationModeStr));
		}
		else
		{
			setOperationModeOperation(Integer.valueOf(operationModeAndParamterModesStr));
		}
	}
}
