'use strict';

describe('Cloudinary Detail Controller', function() {
    var $scope, $rootScope;
    var MockEntity, MockCloudinary;
    var createController;

    beforeEach(inject(function($injector) {
        $rootScope = $injector.get('$rootScope');
        $scope = $rootScope.$new();
        MockEntity = jasmine.createSpy('MockEntity');
        MockCloudinary = jasmine.createSpy('MockCloudinary');
        

        var locals = {
            '$scope': $scope,
            '$rootScope': $rootScope,
            'entity': MockEntity ,
            'Cloudinary': MockCloudinary
        };
        createController = function() {
            $injector.get('$controller')("CloudinaryDetailController", locals);
        };
    }));


    describe('Root Scope Listening', function() {
        it('Unregisters root scope listener upon scope destruction', function() {
            var eventType = 'reachoutApp:cloudinaryUpdate';

            createController();
            expect($rootScope.$$listenerCount[eventType]).toEqual(1);

            $scope.$destroy();
            expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
        });
    });
});
