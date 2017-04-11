'use strict';

describe('PrivateMessage Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockPrivateMessage;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockPrivateMessage = jasmine.createSpy('MockPrivateMessage');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'PrivateMessage': MockPrivateMessage
        };
        createController = function() {
            $injector.get('$controller')("PrivateMessageDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'reachoutApp:privateMessageUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
