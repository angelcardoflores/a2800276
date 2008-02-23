
#include "winscard.h"
#include "pcsclite.h"
#include "ruby.h"

static VALUE rb_mPCSC;
static VALUE rb_cDriver;

static VALUE test_method() {
	printf("test worked!\n");
	return Qtrue;
}

static VALUE 
establishContext(VALUE self) 
{
	SCARDCONTEXT ctx;
	int rv;
	rv = SCardEstablishContext(SCARD_SCOPE_SYSTEM,NULL, NULL, &ctx);
	// TODO error handling rv
	return INT2NUM(ctx);	
}

static VALUE
releaseContext(VALUE self, VALUE ctx) 
{
	int rv = SCardReleaseContext(NUM2INT(ctx));
	//TODO errs
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
	int rv;
	rv = SCardListReaders( NUM2INT(ctx), 
}

void 
Init_PCSC () 
{
	rb_mPCSC = rb_define_module("PCSC");
	rb_cDriver = rb_define_class_under( rb_mPCSC, "Driver", rb_cObject);
	//rb_define_singleton_method(VALUE object, const char *name, VALUE (*func)(), int argc);
	rb_define_singleton_method(rb_cDriver, "establishContext", establishContext, 0);
	rb_define_singleton_method(rb_cDriver, "releaseContext", releaseContext, 1);
	/*rb_define_singleton_method(rb_cDriver, "isValidContext", isValidContext, 1);*/
	rb_define_singleton_method(rb_cDriver, "listReaders", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "listReaderGroups", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "cardConnect", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "cardReconnect", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "cardDisconnect", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "beginTx", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "endTx", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "cardTransmit", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "cardControl", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "getStatus", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "getStatusChange", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "cancelGetStatusChange", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "cardGetAttribute", test_method, 0);//TODO
	rb_define_singleton_method(rb_cDriver, "cardSetAttribute", test_method, 0);//TODO


	printf("worked\n");

}
