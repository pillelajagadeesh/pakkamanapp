'use strict';

describe('DeviceInfo Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockDeviceInfo;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockDeviceInfo = jasmine.createSpy('MockDeviceInfo');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'DeviceInfo': MockDeviceInfo
        };
        createController = function() {
            $injector.get('$controller')("DeviceInfoDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'reachoutApp:deviceInfoUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
