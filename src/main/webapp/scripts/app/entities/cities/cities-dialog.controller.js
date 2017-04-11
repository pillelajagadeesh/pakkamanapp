(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .controller('CitiesDialogController', CitiesDialogController);

    CitiesDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Cities'];

    function CitiesDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Cities) {
        var vm = this;

        vm.cities = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.cities.id !== null) {
                Cities.update(vm.cities, onSaveSuccess, onSaveError);
            } else {
                Cities.save(vm.cities, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('reachoutApp:citiesUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
