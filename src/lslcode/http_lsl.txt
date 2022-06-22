libsl "1.0.0";
library okhttp3;

typealias RandomAccessFile = RandomAccessFile;
typealias MessageDeflater = MessageDeflater;
typealias TaskQueue = TaskQueue;
typealias TaskRunner$Backend = TaskRunner$Backend;
typealias SocketAdapter = SocketAdapter;
typealias Options = Options;
typealias ArrayString = array<string>;
typealias Integer = Integer;
typealias Http2ExchangeCodec$Companion = Http2ExchangeCodec$Companion;
typealias ArrayArraybyte = array<array<byte>>;

...

automaton MultipartBody$Part : MultipartBody$Part {
	initstate Created;

	fun headers(): Headers;
	fun body(): RequestBody;
	fun create(arg0: RequestBody): MultipartBody$Part;
	fun create(arg0: Headers, arg1: RequestBody): MultipartBody$Part;
	fun createFormData(arg0: String, arg1: String): MultipartBody$Part;
	fun createFormData(arg0: String, arg1: String, arg2: RequestBody): MultipartBody$Part;
}

...