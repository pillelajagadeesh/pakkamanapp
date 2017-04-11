(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .controller('CitiesDeleteController',CitiesDeleteController);

    CitiesDeleteController.$inject = ['$uibModalInstance', 'entity', 'Cities'];

    function CitiesDeleteController($uibModalInstance, entity, Cities) {
        var vm = this;

        vm.cities = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Cities.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
