package BasicServer;

import java.nio.ByteBuffer;
public class S2C_BasicClientVerifyResult implements ALBasicProtocolPack._IALProtocolStructure {
private Long socketID;
private String customRetMsg;


public S2C_BasicClientVerifyResult() {
	socketID = (long)0;
	customRetMsg = "";
}

public final byte getMainOrder() { return (byte)0; }

public final byte getSubOrder() { return (byte)0; }

public long getSocketID() { return socketID; }
public void setSocketID(long _socketID) { socketID = _socketID; }
public String getCustomRetMsg() { return customRetMsg; }
public void setCustomRetMsg(String _customRetMsg) { customRetMsg = _customRetMsg; }


public final int GetBufSize() {
	int _size = 8;
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(customRetMsg);

	return _size;
}

public final int GetFullPackBufSize() {
	int _size = 10;
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(customRetMsg);

	return _size;
}



public final void ReadUnzipBuf(ByteBuffer _buf) {
	socketID = _buf.getLong();
	customRetMsg = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
}

public final void PutUnzipBuf(ByteBuffer _buf) {
	_buf.putLong(socketID);
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, customRetMsg);
}

public final ByteBuffer makeFullPackage() {
	int _bufSize = GetBufSize() + 2;
	ByteBuffer _buf = ByteBuffer.allocate(_bufSize);
	_buf.put((byte)0);
	_buf.put((byte)0);
	PutUnzipBuf(_buf);
	_buf.flip();
	return _buf;
}
public final void makeFullPackage(ByteBuffer _recBuf) {
	if(null == _recBuf)
		return ;
	_recBuf.put((byte)0);
	_recBuf.put((byte)0);
	PutUnzipBuf(_recBuf);
	_recBuf.flip();
}
public final ByteBuffer makePackage() {
	int _bufSize = GetBufSize();
	ByteBuffer _buf = ByteBuffer.allocate(_bufSize);
	PutUnzipBuf(_buf);
	_buf.flip();
	return _buf;
}
public final void readPackage(ByteBuffer _buf) {
	ReadUnzipBuf(_buf);
}
}

