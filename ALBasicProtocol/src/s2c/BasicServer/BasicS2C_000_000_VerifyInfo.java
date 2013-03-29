package s2c.BasicServer;

import java.nio.ByteBuffer;
public class BasicS2C_000_000_VerifyInfo implements ALBasicProtocolPack.IALProtocolStructure {
private String userName;
private Integer socketID;


public BasicS2C_000_000_VerifyInfo() {
	userName = "";
	socketID = 0;
}

public final byte getMainOrder() { return (byte)0; }

public final byte getSubOrder() { return (byte)0; }

public String getUserName() { return userName; }
public void setUserName(String _userName) { userName = _userName; }
public int getSocketID() { return socketID; }
public void setSocketID(int _socketID) { socketID = _socketID; }


public final int GetUnzipBufSize() {
	int _size = 4;
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(userName);

	return _size;
}

public final int GetZipBufSize() {
	int _size = 0;
	_size += ALBasicProtocolPack.ALProtocolCommon.GetStringBufSize(userName);
	_size += ALBasicProtocolPack.ALProtocolCommon.GetIntZipSize(socketID);

	return _size;
}



public final void ReadUnzipBuf(ByteBuffer _buf) {
	userName = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
	socketID = _buf.getInt();
}

public final void ReadZipBuf(ByteBuffer _buf) {
	userName = ALBasicProtocolPack.ALProtocolCommon.GetStringFromBuf(_buf);
	socketID = ALBasicProtocolPack.ALProtocolCommon.ZipGetIntFromBuf(_buf);
}

public final void PutUnzipBuf(ByteBuffer _buf) {
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, userName);
	_buf.putInt(socketID);
}

public final void PutZipBuf(ByteBuffer _buf) {
	ALBasicProtocolPack.ALProtocolCommon.PutStringIntoBuf(_buf, userName);
	ALBasicProtocolPack.ALProtocolCommon.ZipPutIntIntoBuf(_buf, socketID);
}

public final ByteBuffer makeFullPackage() {
	int _bufSize = GetUnzipBufSize() + 2;
	ByteBuffer _buf = ByteBuffer.allocate(_bufSize);
	_buf.put((byte)0);
	_buf.put((byte)0);
	PutUnzipBuf(_buf);
	_buf.flip();
	return _buf;
}
public final ByteBuffer makePackage() {
	int _bufSize = GetUnzipBufSize();
	ByteBuffer _buf = ByteBuffer.allocate(_bufSize);
	PutUnzipBuf(_buf);
	_buf.flip();
	return _buf;
}
public final void readPackage(ByteBuffer _buf) {
	ReadUnzipBuf(_buf);
}
}

