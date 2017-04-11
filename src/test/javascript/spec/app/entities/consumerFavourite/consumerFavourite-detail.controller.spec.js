'use strict';

describe('ConsumerFavourite Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockConsumerFavourite;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockConsumerFavourite = jasmine.createSpy('MockConsumerFavourite');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'ConsumerFavourite': MockConsumerFavourite
        };
        createController = function() {
            $injector.get('$controller')("ConsumerFavouriteDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'reachoutApp:consumerFavouriteUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
