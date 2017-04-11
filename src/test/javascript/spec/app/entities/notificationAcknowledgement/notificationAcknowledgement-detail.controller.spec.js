'use strict';

describe('NotificationAcknowledgement Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockNotificationAcknowledgement;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockNotificationAcknowledgement = jasmine.createSpy('MockNotificationAcknowledgement');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'NotificationAcknowledgement': MockNotificationAcknowledgement
        };
        createController = function() {
            $injector.get('$controller')("NotificationAcknowledgementDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'reachoutApp:notificationAcknowledgementUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
