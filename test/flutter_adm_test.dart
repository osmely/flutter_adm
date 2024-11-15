import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_adm/flutter_adm.dart';
import 'package:flutter_adm/flutter_adm_platform_interface.dart';
import 'package:flutter_adm/flutter_adm_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterAdmPlatform
    with MockPlatformInterfaceMixin
    implements FlutterAdmPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterAdmPlatform initialPlatform = FlutterAdmPlatform.instance;

  test('$MethodChannelFlutterAdm is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterAdm>());
  });

  test('getPlatformVersion', () async {
    FlutterAdm flutterAdmPlugin = FlutterAdm();
    MockFlutterAdmPlatform fakePlatform = MockFlutterAdmPlatform();
    FlutterAdmPlatform.instance = fakePlatform;

    expect(await flutterAdmPlugin.getPlatformVersion(), '42');
  });
}
