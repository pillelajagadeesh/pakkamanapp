'use strict';

angular.module('reachoutApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('provider', {
                parent: 'entity',
                url: '/providers',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.provider.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/provider/providers.html',
                        controller: 'ProviderController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('provider');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('map', {
                parent: 'entity',
                url: '/location/map',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.provider.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/provider/map.html',
                        controller: 'MapController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('map');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            
            .state('provider.detail', {
                parent: 'entity',
                url: '/provider/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.provider.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/provider/provider-detail.html',
                        controller: 'ProviderDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('provider');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Provider', function($stateParams, Provider) {
                        return Provider.get({id : $stateParams.id});
                    }]
                }
            })
           .state('provider.createpromo', {
               parent: 'provider',
               url: '/{id}/{promo}/provider',
               data: {
                   authorities: ['ROLE_USER'],
               },
               onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                   $uibModal.open({
                       templateUrl: 'scripts/app/entities/provider/promo-credit.html',
                       controller: 'PromoCreditController',
                       size: 'md',
                       resolve: {
                           translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                               $translatePartialLoader.addPart('provider');
                               $translatePartialLoader.addPart('global');
                               return $translate.refresh();
                           }]
                       }
                   }).result.then(function(result) {
                       $state.go('provider', null, { reload: true });
                   }, function() {
                       $state.go('^');
                   })
               }]
           })
           .state('provider.bannercheck', {
               parent: 'notification',
               url: '/{id}/{banner}/provider',
               data: {
                   authorities: ['ROLE_USER'],
               },
               onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                   $uibModal.open({
                       templateUrl: 'scripts/app/entities/provider/banner-check.html',
                       controller: 'BannerCheckController',
                       size: 'md',
                       resolve: {
                           translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                               $translatePartialLoader.addPart('provider');
                               $translatePartialLoader.addPart('global');
                               return $translate.refresh();
                           }]
                       }
                   }).result.then(function(result) {
                       $state.go('notification', null, { reload: true });
                   }, function() {
                       $state.go('^');
                   })
               }]
           })
            .state('provider.new', {
                parent: 'provider',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/provider/provider-dialog.html',
                        controller: 'ProviderDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    mobile: null,
                                    email: null,
                                    name: null,
                                    address: null,
                                    latitude: null,
                                    longitude: null,
                                    updated: null,
                                    consumerId: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('provider', null, { reload: true });
                    }, function() {
                        $state.go('provider');
                    })
                }]
            })
             
            .state('provider.edit', {
                parent: 'provider',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/provider/provider-dialog.html',
                        controller: 'ProviderDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Provider', function(Provider) {
                                return Provider.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('provider', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('provider.view', {
                parent: 'entity',
                url: '/provider/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.consumer.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/provider/provider-detail.html',
                        controller: 'ProviderDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('provider');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Provider', function($stateParams, Provider) {
                        return Provider.get({id : $stateParams.id});
                    }]
                }
            })
             .state('consumer.view', {
                parent: 'entity',
                url: '/consumer/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.consumer.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumer/consumer-detail.html',
                        controller: 'ConsumerDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumer');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Consumer', function($stateParams, Consumer) {
                        return Consumer.get({id : $stateParams.id});
                    }]
                }
            })
    });
