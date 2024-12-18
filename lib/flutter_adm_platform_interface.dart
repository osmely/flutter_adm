import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_adm_method_channel.dart';

abstract class FlutterAdmPlatform extends PlatformInterface {
  FlutterAdmPlatform() : super(token: _token);
  static final Object _token = Object();
  static FlutterAdmPlatform _instance = MethodChannelFlutterAdm();
  static FlutterAdmPlatform get instance => _instance;

  static set instance(FlutterAdmPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  void startRegister(){
    throw UnimplementedError('startRegister() has not been implemented.');
  }

  void startUnregister(){
    throw UnimplementedError('startUnregister() has not been implemented.');
  }  

  void initialize() {
    throw UnimplementedError('initialize() has not been implemented.');
  }

  void setOnRegistrationId(Function(String) callback) { 
    throw UnimplementedError('setOnRegistrationId() has not been implemented.');
  }

  void setOnMessage(Function(String) callback) {
    throw UnimplementedError('setOnMessage() has not been implemented.');
  }

  void setOnNotificationClicked(Function(String) callback) {
    throw UnimplementedError('setOnNotificationClicked() has not been implemented.');
  }

  Future<bool> isSupported() async {
    throw UnimplementedError('isSupported() has not been implemented.');
  }

  Future<void> setTopicSubscription(String topic, bool suscribe) async {
    throw UnimplementedError('setTopicSubscription() has not been implemented.');
  }

  Future<String?> getInitialMessage() async {
    throw UnimplementedError('getInitialMessage() has not been implemented.');
  }

  Future<String?> getRegistrationId() async {
    throw UnimplementedError('getRegistrationId() has not been implemented.');
  }

}
