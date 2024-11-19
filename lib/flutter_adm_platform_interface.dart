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

  Future<String?> getRegistrationId() {
    throw UnimplementedError('getRegistrationId() has not been implemented.');
  }

  Future suscribeToTopic(String topic) {
    throw UnimplementedError('suscribeToTopic() has not been implemented.');
  }

  void startRegister() {
    throw UnimplementedError('startRegister() has not been implemented.');
  }
}
