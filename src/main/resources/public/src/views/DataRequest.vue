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
                    v-model="dataRequest.organization"
                    v-bind:data="organizations"
                    placeholder="Find a Collectivity"
                    v-bind:minMatchingChars="minMatch"
                    v-bind:maxMatches="maxMatch"
                    ref="clamercollectivity"
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
                organizations: [],
                errors: [],
                response: {},
                minMatch: 0,
                maxMatch: 50
            }
        },
        computed: {
            disabled: function(){
                return(this.dataRequest.organization == '' || this.dataRequest.email == '' || (this.dataRequest.model == '' || this.dataRequest.model === null))
            }
        },
        beforeCreate() {
            
            if(this.$route.params.id != null) {
                axios.get(`/api/data_access_request/${this.$route.params.id}`)
                  .then(response => {
                    this.dataRequest = response.data
                    //Even if typeahead value = dataRequest.model - we need to fullfill typeahead inputValue to display value
                    this.$refs.typeahead.$data.inputValue = this.dataRequest.model
                    this.$refs.clamercollectivity.$data.inputValue = this.dataRequest.organization
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

            //Get first hundred organization name from datacore
            axios.get('/api/data_access_request/organisation?name=')
                .then(response => {
                    response.data.forEach( data => {
                        this.organizations.push(data.denominationUniteLegale)
                    })
                })
                .catch(e => {
                    this.errors.push(e)
                })

            console.log(this.errors)
            
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
            //Empty typeahead inputValue when updating route
            this.$refs.typeahead.$data.inputValue = ''
            this.$refs.clamercollectivity.$data.inputValue = ''
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
            getModels(name){
                axios.get('/api/data_access_request/123456789/model?name=' + name)
                .then(response => {
                    this.models = response.data
                })
                .catch(e => {
                    this.errors.push(e)
                })
            },
            getOrganizations(name){
                this.organizations = []
                axios.get('/api/data_access_request/organisation?name=' + name)
                .then(response => {
                    response.data.forEach( data => {
                        this.organizations.push(data.denominationUniteLegale)
                    })
                })
                .catch(e => {
                    this.errors.push(e)
                })
            }
        },
        watch: {
            dataRequest: {
                handler: function (val, oldVal) {
                    if(val.model != '' && val.model != null)
                        this.getModels(val.model)
                    if(val.organization != '' && val.organization != null)
                        this.getOrganizations(val.organization)
                },
                deep: true
            }
        }
    }
</script>

<style scoped>

</style>
