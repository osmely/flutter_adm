#import "FlutterAdmPlugin.h"

@implementation FlutterAdmPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_adm"
            binaryMessenger:[registrar messenger]];
  FlutterAdmPlugin* instance = [[FlutterAdmPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  // Implementación vacía para todos los métodos, ya que ADM no está disponible en iOS
  if ([@"initialize" isEqualToString:call.method]) {
    result(nil);
  } else if ([@"getRegistrationId" isEqualToString:call.method]) {
    result(nil);
  } else if ([@"isSupported" isEqualToString:call.method]) {
    result(@(NO));  // Siempre devuelve false ya que ADM no es compatible con iOS
  } else {
    result(FlutterMethodNotImplemented);
  }
}

@end
