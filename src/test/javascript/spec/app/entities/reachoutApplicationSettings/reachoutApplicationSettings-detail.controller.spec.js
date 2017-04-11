'use strict';

describe('ApplicationSettings Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockApplicationSettings;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockApplicationSettings = jasmine.createSpy('MockApplicationSettings');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'ApplicationSettings': MockApplicationSettings
        };
        createController = function() {
            $injector.get('$controller')("ApplicationSettingsDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'reachoutApp:reachoutApplicationSettingsUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
