
#include "winscard.h"
#include "ruby.h"

/* from pcsclite.h */
#define MAX_ATR_SIZE                      33 
/* stupid guess TODO!*/
#define MAX_APDU_SIZE                     33 

static VALUE rb_mPCSC;
static VALUE rb_cDriver;
static VALUE rb_cPCSCException;

static VALUE test_method() {
	printf("test worked!\n");
	return Qtrue;
}

static void 
pcsc_check_exception(int rv) {
	if (rv != SCARD_S_SUCCESS) {
		rb_raise(rb_cPCSCException, "%s (%x)", pcsc_stringify_error(rv), rv);
	}
}

static VALUE 
establishContext(VALUE self) 
{
	SCARDCONTEXT ctx;
	
	pcsc_check_exception( 
		SCardEstablishContext(SCARD_SCOPE_SYSTEM,NULL, NULL, &ctx) 
	);
	
	return INT2NUM(ctx);	
}

static VALUE
releaseContext(VALUE self, VALUE ctx) 
{
	pcsc_check_exception (
		SCardReleaseContext(NUM2INT(ctx))
	);
	
	return Qtrue;
}

/*
static VALUE
isValidContext(VALUE self, VALUE ctx) 
{
	int rv = SCardIsValidContext(NUM2INT(ctx));
	return INT2NUM(rv);
}
*/

static VALUE
listReaders(VALUE self, VALUE ctx) 
{
	unsigned int size;
	char *readers;
	VALUE rb_readers;

		  
	pcsc_check_exception (
		SCardListReaders( NUM2INT(ctx), NULL, NULL, &size)
	);
	
	readers = malloc(sizeof(char)*size);
	pcsc_check_exception (
		SCardListReaders( NUM2INT(ctx), NULL, readers, &size)
	);
	rb_readers = rb_str_new(readers, size);
	free(readers);
	return rb_readers;

}

/*
 * Returns and array [handle, protocol]
 *
 * */
static VALUE
cardConnect(VALUE self, VALUE ctx, VALUE reader, VALUE shared, VALUE proto){
	int card;
	unsigned int actProto;

	/*int ctx, shared;
	char * reader;*/

	// printf ("ctx: %d, reader: %s, shared: %d, proto %d\n", NUM2INT(ctx), StringValueCStr(reader), NUM2INT(shared), NUM2INT(proto));
	
	pcsc_check_exception (
		 SCardConnect( 
			NUM2INT(ctx), 
			StringValueCStr(reader), 
			NUM2INT(shared), 
			NUM2INT(proto),
			&card,
			&actProto)
	);

	/* printf ("c_hndl: %d, proto: %d\n", card, actProto);*/
	return rb_ary_new3(2,INT2NUM(card), INT2NUM(actProto));
}

static VALUE
cardDisconnect(VALUE self, VALUE card, VALUE disposition) 
{
	pcsc_check_exception (
		SCardDisconnect (NUM2INT(card), NUM2INT(disposition))
	);

	return Qtrue;
}

static VALUE
beginTx(VALUE self, VALUE card) 
{
	pcsc_check_exception (
		SCardBeginTransaction(NUM2INT(card))
	);
	return Qtrue;
}

static VALUE
endTx(VALUE self, VALUE card, VALUE disposition) 
{
	pcsc_check_exception(
		SCardEndTransaction(NUM2INT(card), NUM2INT(disposition))
	);
	return Qtrue;
}

/**
 *
 * things start getting more complicated...
 */
static VALUE 
cardTransmit(VALUE self, VALUE card, VALUE send, VALUE proto) {
	int rv;
	SCARD_IO_REQUEST *sendPci, recvPci;
	unsigned char recvBuffer[MAX_APDU_SIZE]; // TODO
	unsigned int recvLen = sizeof(recvBuffer);

	unsigned char *sendBuffer;
	unsigned int sendLen;

	sendPci = (NUM2INT(proto) == SCARD_PROTOCOL_T0) 
			? SCARD_PCI_T0 
			: SCARD_PCI_T1;
	// TODO some more error handling in case proto matches neither T0 or T1...
	// a nice `switch` block maybe?
	
	//TODO sanity check concerning arg types?
	sendBuffer = (unsigned char*)StringValuePtr(send);
	sendLen = RSTRING(send)->len;
	

	pcsc_check_exception (
		SCardTransmit(
			NUM2INT(card),
			sendPci,
			sendBuffer,
			sendLen,	
			&recvPci,
			recvBuffer,
			&recvLen
			)
	);
	return rb_str_new((char*)recvBuffer, recvLen);
}

static VALUE
getStatus (VALUE self, VALUE card) 
{
	unsigned int state, proto, atrLen, bullshitLen;
	unsigned char atrBuf[MAX_ATR_SIZE];
	char bullshit[100];

	
	atrLen = sizeof(atrBuf);
	bullshitLen = sizeof(bullshit);

	
	pcsc_check_exception (
		SCardStatus(
			NUM2INT(card),
			bullshit, /* reader Name*/
			&bullshitLen, /* reader size*/
			&state, /* state */
			&proto, /* proto */
			atrBuf,
			&atrLen
		)
	);
	
	printf ("rv: %d state: %d, proto: %d, len: %d\n", 0, state, proto, atrLen);
	printf ("name: %s", bullshit);

	// TODO : reader name? What's the use?
	return rb_ary_new3(3,INT2NUM(state),INT2NUM(proto),rb_str_new((char*)atrBuf, atrLen));

}


void 
Init_PCSC_NATIVE () 
{
	rb_mPCSC = rb_define_module("PCSC");
	rb_cDriver = rb_define_class_under( rb_mPCSC, "Driver", rb_cObject);
	rb_cPCSCException = rb_define_class_under( rb_mPCSC, "PCSCException", rb_eRuntimeError);
	//rb_define_singleton_method(VALUE object, const char *name, VALUE (*func)(), int argc);
	rb_define_singleton_method(rb_cDriver, "establishContext", establishContext, 0);
	rb_define_singleton_method(rb_cDriver, "releaseContext", releaseContext, 1);
//	/*rb_define_singleton_method(rb_cDriver, "isValidContext", isValidContext, 1);*/ //TODO
	rb_define_singleton_method(rb_cDriver, "listReaders", listReaders, 1);
//	rb_define_singleton_method(rb_cDriver, "listReaderGroups", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "cardConnect", cardConnect, 4);
//	rb_define_singleton_method(rb_cDriver, "cardReconnect", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "cardDisconnect", cardDisconnect, 2);
	rb_define_singleton_method(rb_cDriver, "beginTx", beginTx, 1);
	rb_define_singleton_method(rb_cDriver, "endTx", endTx, 2);
	rb_define_singleton_method(rb_cDriver, "cardTransmit", cardTransmit, 3);
//	rb_define_singleton_method(rb_cDriver, "control", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "getStatus", getStatus, 1);
//	rb_define_singleton_method(rb_cDriver, "getStatusChange", test_method, 0);//TODO
//	rb_define_singleton_method(rb_cDriver, "cancelGetStatusChange", test_method, 0);//TODO
//	rb_define_singleton_method(rb_cDriver, "cardGetAttribute", test_method, 0);//TODO
//	rb_define_singleton_method(rb_cDriver, "cardSetAttribute", test_method, 0);//TODO


	printf("worked\n");

}
