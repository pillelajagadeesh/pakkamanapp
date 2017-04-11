'use strict';

describe('ConsumerRegions Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockConsumerRegions;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockConsumerRegions = jasmine.createSpy('MockConsumerRegions');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'ConsumerRegions': MockConsumerRegions
        };
        createController = function() {
            $injector.get('$controller')("ConsumerRegionsDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'reachoutApp:consumerRegionsUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
