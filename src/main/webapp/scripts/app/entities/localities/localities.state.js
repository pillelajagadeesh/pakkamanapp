(function() {
    'use strict';

    angular
        .module('reachoutApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('localities', {
            parent: 'entity',
            url: '/localities',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'reachoutApp.localities.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/entities/localities/localities.html',
                    controller: 'LocalitiesController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('localities');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('localities-detail', {
            parent: 'entity',
            url: '/localities/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'reachoutApp.localities.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/entities/localities/localities-detail.html',
                    controller: 'LocalitiesDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('localities');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Localities', function($stateParams, Localities) {
                    return Localities.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('localities.new', {
            parent: 'localities',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'scripts/app/entities/localities/localities-dialog.html',
                    controller: 'LocalitiesDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                localityname: null,
                                localitylocation: null,
                                cityname: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('localities', null, { reload: true });
                }, function() {
                    $state.go('localities');
                });
            }]
        })
        .state('localities.edit', {
            parent: 'localities',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'scripts/app/entities/localities/localities-dialog.html',
                    controller: 'LocalitiesDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Localities', function(Localities) {
                            return Localities.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('localities', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('localities.delete', {
            parent: 'localities',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'scripts/app/entities/localities/localities-delete-dialog.html',
                    controller: 'LocalitiesDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Localities', function(Localities) {
                            return Localities.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('localities', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
