'use strict';

describe('ConsumerFeedback Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockConsumerFeedback;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockConsumerFeedback = jasmine.createSpy('MockConsumerFeedback');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'ConsumerFeedback': MockConsumerFeedback
        };
        createController = function() {
            $injector.get('$controller')("ConsumerFeedbackDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'reachoutApp:consumerFeedbackUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
