package ALBasicCommon;

public class ALSerializeMaker
{
	private static long _g_lSerialize = 1;
	/**********
	 * 构造一个新序列号并返回
	 * @return
	 */
	public synchronized static long makeNewSerialize()
	{
		long serialize = _g_lSerialize;
		_g_lSerialize++;
		
		return serialize;
	}
}
