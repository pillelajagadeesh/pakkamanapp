(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .controller('CitiesDetailController', CitiesDetailController);

    CitiesDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Cities'];

    function CitiesDetailController($scope, $rootScope, $stateParams, entity, Cities) {
        var vm = this;

        vm.cities = entity;

        var unsubscribe = $rootScope.$on('reachoutApp:citiesUpdate', function(event, result) {
            vm.cities = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
