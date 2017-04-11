(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .controller('PakkaApplicationSettingsDetailController', PakkaApplicationSettingsDetailController);

    PakkaApplicationSettingsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'PakkaApplicationSettings'];

    function PakkaApplicationSettingsDetailController($scope, $rootScope, $stateParams, entity, PakkaApplicationSettings) {
        var vm = this;

        vm.pakkaApplicationSettings = entity;

        var unsubscribe = $rootScope.$on('reachoutApp:pakkaApplicationSettingsUpdate', function(event, result) {
            vm.pakkaApplicationSettings = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
