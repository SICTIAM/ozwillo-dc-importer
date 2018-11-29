<template>
    <div id="container" class="container">
        <h2>Request for access to dataset</h2>
        <form>
            <div class="form-group row">
                <label for="claimer-collectivity" class="col-sm-3 col-form-label col-form-label-sm">
                    Organization
                </label>
                <vue-bootstrap-typeahead
                    id="claimer-collectivity"
                    v-model="organization.nameAndSiret"
                    v-bind:data="organizationsNameList"
                    placeholder="Find a Collectivity"
                    v-bind:minMatchingChars="minMatch"
                    v-bind:maxMatches="maxMatch"
                    ref="claimercollectivity"
                />
            </div>
            <div class="form-group row">
                <label for="claimer-email" class="col-sm-3 col-form-label col-form-label-sm">
                    Email
                </label>
                <input id="claimer-email" v-model="dataRequest.email"/>
            </div>
            <div class="form-group row">
                <label for="claimer-model" class="col-sm-3 col-form-label col-form-label-sm">
                    Model
                </label>
                <vue-bootstrap-typeahead 
                    v-model="dataRequest.model"
                    v-bind:data="models"
                    placeholder="Find a model dataset"
                    v-bind:minMatchingChars="minMatch"
                    v-bind:maxMatches="maxMatch"
                    ref="typeahead"
                />
            </div>
            <input type="button" @click="createDataRequestModel()" value="submit" v-bind:disabled="disabled">
        </form>
    </div>
</template>

<script>
    import axios from 'axios'
    import _ from 'lodash'
    import VueBootstrapTypeahead from 'vue-bootstrap-typeahead'
    import  VueRouter from 'vue-router'

    export default {
        name: "DataRequest",
        components: {
          VueBootstrapTypeahead  
        },
        data() {
            return {
                dataRequest: {
                    id: null,
                    nom: '',
                    email: '',
                    organization: '',
                    model: ''
                },
                models: [],
                organization: {
                        legalName: '',
                        siret: '',
                        nameAndSiret: ''
                    },
                organizationsNameList: [],
                errors: [],
                response: {},
                minMatch: 0,
                maxMatch: 50
            }
        },
        watch: {
            dataRequest: {
                handler: function (val, oldVal) {
                    if(val.model != '' && val.model != null)
                        this.debouncedGetModels()
                },
                deep: true
            },
            organization: {
                handler (val, oldVal){
                    this.organization.legalName = this.organization.nameAndSiret.split(' //')[0]
                    this.organization.siret = this.organization.nameAndSiret.split('// ')[1]
                    this.dataRequest.organization = "http://data.ozwillo.com/dc/type/orgfr:Organisation_0/FR/" + this.organization.siret
                    this.debouncedGetOrganizations()
                },
                deep: true
            }
        },
        computed: {
            disabled: function(){
                return(this.organization.legalName == '' || this.dataRequest.email == '' || (this.dataRequest.model == '' || this.dataRequest.model === null))
            }
        },
        beforeCreate() {
            
            if(this.$route.params.id != null) {
                axios.get(`/api/data_access_request/${this.$route.params.id}`)
                  .then(response => {
                    this.dataRequest = response.data

                    this.updateOrganizationBySiret()

                    //Even if typeahead value = dataRequest.model - we need to fullfill typeahead inputValue to display value
                    this.$refs.typeahead.$data.inputValue = this.dataRequest.model
                  })
                  .catch(e => {
                    this.errors.push(e)
                  })
            }

            //Get first hundred models from datacore
            axios.get('/api/data_access_request/123456789/model')
                .then(response => {
                    this.models = response.data
                })
                .catch(e => {
                    this.errors.push(e)
                })
        },
        created (){

            //Get first hundred organization name from datacore
            this.getOrganizations()

            this.debouncedGetModels = _.debounce(this.getModels, 500)
            this.debouncedGetOrganizations = _.debounce(this.getOrganizations, 500)
        },
        beforeRouteUpdate (to, from, next) {
            next()
            this.dataRequest = {
                id: null,
                    nom: '',
                    email: '',
                    organization: '',
                    model: ''
            }
            this.organization = {
                        legalName: '',
                        siret: '',
                        nameAndSiret: ''
                    }
            //Empty typeahead inputValue when updating route
            this.$refs.typeahead.$data.inputValue = ''
            this.$refs.claimercollectivity.$data.inputValue = ''
        },
        methods: {
            createDataRequestModel() {
                axios.post(`/api/data_access_request/123456789/send`, this.dataRequest)
                    .then(response => {
                        this.response = response.data
                        this.$router.push({ name: 'dashboard' })
                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
            },
            getModels (){
                axios.get('/api/data_access_request/123456789/model?name=' + this.dataRequest.model)
                .then(response => {
                    this.models = response.data
                })
                .catch(e => {
                    this.errors.push(e)
                })
            },
            getOrganizations (){
                this.organizationsNameList = []
                axios.get('/api/data_access_request/organizations?name=' + this.organization.legalName)
                .then(response => {
                    response.data.forEach( data => {
                        this.organizationsNameList.push(data.denominationUniteLegale + ' // ' + data.siret)
                    })
                })
                .catch(e => {
                    this.errors.push(e)
                })
            },
            updateOrganizationBySiret (){
                var splitedUri = this.dataRequest.organization.split("/")
                var siret = splitedUri[splitedUri.length - 1]

                axios.get('/api/data_access_request/organizations?siret=' + siret)
                .then(response => {
                    this.organization.legalName = response.data[0].denominationUniteLegale
                    this.organization.nameAndSiret = response.data[0].denominationUniteLegale + ' // ' + siret
                    this.$refs.claimercollectivity.$data.inputValue = this.organization.nameAndSiret
                })
                .catch(e => {
                    this.errors.push(e)
                })
            }
        }
    }
</script>

<style scoped>

</style>
