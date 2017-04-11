(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .controller('LocalitiesDetailController', LocalitiesDetailController);

    LocalitiesDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Localities'];

    function LocalitiesDetailController($scope, $rootScope, $stateParams, entity, Localities) {
        var vm = this;

        vm.localities = entity;

        var unsubscribe = $rootScope.$on('reachoutApp:localitiesUpdate', function(event, result) {
            vm.localities = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
