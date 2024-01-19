<template>
  <v-card theme="dark">

      <v-sheet v-if="productCount!=null"
    class="d-flex align-center justify-center flex-wrap text-center mx-auto px-4"
    style="position: fixed; top: 0; left: 0; right: 0; bottom: 0; z-index: 9999;"
    elevation="20"
    height="50"
    rounded
    max-width="800"
    width="100%"
  >
    <div>
      <h2 class="text-h4 font-weight-black text-orange">NUMBER OF PRODUCTS: {{productCount}}</h2>
    </div>
  </v-sheet>
    <v-layout theme="dark">
    <div @click="handleCanvasClick" id="konva-container"></div>
      <v-navigation-drawer
        v-model="drawer"
        :rail="rail"
        permanent
        app
      >
        <v-list density="compact" nav>
          <v-tooltip v-for="item in menuItems" :key="item.value" :label="item.title" position="right">
            <template v-slot:activator="{ props }">
              <v-list-item v-bind="props" :active="isItemActive(item.value)" :prepend-icon="item.icon" :title="item.title" :value="item.value" @click="handleMenuItemClick(item)" >
              </v-list-item>
            </template>
            <span>{{item.title}}</span>
          </v-tooltip>
        </v-list>
      </v-navigation-drawer>
    </v-layout>
  </v-card>
</template>

<script>
import Konva from 'konva';
import axios from 'axios';

export default {
  data() {
    return {
      drawer: true,
      rail: true,
      menuItems: [
        { title: 'Queue', value: 'queue', icon: 'mdi-crosshairs-question' },
        { title: 'Machine', value: 'machine', icon: 'mdi-router-network' },
        { title: 'Connect', value: 'connect', icon: 'mdi-arrow-top-right' },
        { title: 'Play', value: 'play', icon: 'mdi-play-circle' },
        { title: 'Increase Product', value: 'add', icon: 'mdi-plus-circle' },
        { title: 'Decrease Product', value: 'minus', icon: 'mdi-minus-circle' },
        { title: 'Reload', value: 'reload', icon: 'mdi-reload' },
        { title: 'Clear', value: 'clear', icon: 'mdi-layers-off' },
      ],
      stage: null,
      layer: null,
      selectedShape: null,
      startShape: null,
      endShape: null,
      lastshapeclicked: null,
      productCount: 0,
      circles:[],
      squares:[],

    };
  },
  methods: {
    isItemActive(value) {
      // return value !== 'add' && value !== 'minus';
      if (value === 'add' || value === 'minus' || value === 'play' || value === 'reload' || value === 'clear') {
        return false;
      }
      return null
    },
    handleMenuItemClick(item) {
      if (item.value === this.selectedShape && item.value != 'add' && item.value != 'minus') {
        this.selectedShape = null;
      }
      else{
        this.selectedShape = item.value;
      }
      // Clear start and end shapes when a menu item is clicked
      this.startShape = null;
      this.endShape = null;
      this.lastshapeclicked = null;
      if (this.selectedShape === 'add') {
        this.addProduct();
      }
      else if(this.selectedShape === 'minus'){
        this.removeProduct();
      }
      else if(this.selectedShape === 'play'){
        this.play();
      }
      else if(this.selectedShape === 'reload'){
        this.reload();
      }
      else if(this.selectedShape === 'clear'){
        this.clear();
      }
    },
    play() {
      axios.post('http://localhost:5000/start', {
        numberOfProducts: this.productCount,
      }, {
        headers: {
          'Content-Type': 'application/json',
        },
      })
        .then((response) => {
          console.log(response);
        })
        .catch((error) => {
          console.error(error);
        });
    },
    reload(){
      axios.post('http://localhost:5000/reload', {
      })
        .then((response) => {
          console.log(response);
        })
        .catch((error) => {
          console.error(error);
        });
    },
    clear(){
      axios.post('http://localhost:5000/clear', {
      })
        .then((response) => {
          console.log(response);
          if(response.data == "success"){
            this.stage.destroyChildren();
            this.stage.draw();
            this.circles = [];
            this.squares = [];
          }
        })
        .catch((error) => {
          console.error(error);
        });
    },
    addProduct(){
      this.productCount++;
    },
    removeProduct(){
      if (this.productCount > 0) {
        this.productCount--;
      }
    },
    handleCanvasClick(event) {
      const container = this.$el.querySelector('#konva-container');
      const rect = container.getBoundingClientRect();
      const clickX = event.clientX - rect.left;
      const clickY = event.clientY - rect.top;

      if (this.selectedShape === 'connect') {
        this.handleConnectClick(clickX, clickY);
      } else if (this.selectedShape === 'queue') {
        this.drawCircle(clickX, clickY);
      } else if (this.selectedShape === 'machine') {
        this.drawSquare(clickX, clickY);
      }
    },
    async drawCircle(x, y) {
      try {
        // Make an asynchronous request to the backend
        // const response = await axios.get('http://localhost:5000/createqueue');

        // Extract the ID from the response data
        // const circleID = response.data;
        const circleID = 1;
        console.log(circleID);

        // Create the Konva circle
        const circle = new Konva.Circle({
          x,
          y,
          radius: 25,
          fill: 'orange',
          id: 'Q'+circleID, // will be used to identify the circle later 
          draggable: false,
        });

        // Attach click event handler to the circle
        circle.on('click', () => {
          this.handleShapeClick(circle);
        });

        // Add the circle to the layer
        this.layer.add(circle);

        // Create the Konva text
        const text = new Konva.Text({
          x,
          y,
          text: 'Q' + circleID,
          fontSize: 12,
          fill: 'black',
          width: 40,
          align: 'center',
        });
        text.offsetX(text.width() / 2);
        text.offsetY(text.height() / 2);

        // Attach click event handler to the text
        text.on('click', () => {
          this.handleShapeClick(circle);
        });

        // Add the text to the layer
        this.layer.add(text);

        // Redraw the stage
        this.stage.draw();

        // Add the circle and text information to the circles array
        this.circles.push({ id: circleID, circle, text });
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    },

    async drawSquare(x, y) {
      try {
        // Make an asynchronous request to the backend
        // const response = await axios.get('http://localhost:5000/createmachine');

        // Extract the ID from the response data
        // const squareID = response.data;
        const squareID = 1;
        console.log(squareID);

        // Create the Konva square
        const square = new Konva.Rect({
          x,
          y,
          width: 50,
          height: 50,
          id: 'M'+squareID, // will be used to identify the square later 
          fill: '#864AF9',
          draggable: false,
        });

        // Attach click event handler to the square
        square.on('click', () => {
          this.handleShapeClick(square);
        });

        // Add the square to the layer
        this.layer.add(square);

        // Create the Konva text
        const text = new Konva.Text({
          x: x + 25,
          y: y + 25,
          text: 'M' + squareID,
          fontSize: 12,
          fill: 'white',
          width: 40,
          align: 'center',
        });

        text.offsetX(text.width() / 2);
        text.offsetY(text.height() / 2);

        // Attach click event handler to the text
        text.on('click', () => {
          this.handleShapeClick(square);
        });

        // Add the text to the layer
        this.layer.add(text);

        // Redraw the stage
        this.stage.draw();

        // Add the square and text information to the squares array
        this.squares.push({ id: squareID, square, text });
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    },
    handleShapeClick(shape) {
      console.log('Shape clicked');
      this.lastshapeclicked = shape;
    },
    handleConnectClick(clickX, clickY) {
      // this.detect_queue(0); ///for testing the detect functions
      // this.detect_machine(1);
      console.log(this.lastshapeclicked);
      const clickedShape = this.lastshapeclicked;
      if (clickedShape) {
        if (!this.startShape) {
          this.startShape = clickedShape;
        } else {
          this.endShape = clickedShape;
          this.connectShapes();
          this.startShape = null;
          this.endShape = null;
          this.lastshapeclicked = null;
        }
      }
    },
    drawArrow() {
      const dx = this.endShape.x() - this.startShape.x();
      const dy = this.endShape.y() - this.startShape.y();
      let angle = Math.atan2(-dy, dx);
      // console.log(this.startShape.id(), this.endShape.id());

      const radius = 48;
      let from = {
        x: this.startShape.x(),
        y: this.startShape.y(),
      };

      let to = {
        x: this.endShape.x(),
        y: this.endShape.y(),
      };

      if (this.startShape.getClassName() === 'Rect') {
        from.x += 25;
        from.y += 25;
      }

      if (this.endShape.getClassName() === 'Rect') {
        to.x += 25;
        to.y += 25;
      }

      const arrow = new Konva.Arrow({
        points: [
          from.x + -radius * Math.cos(angle + Math.PI),
          from.y + radius * Math.sin(angle + Math.PI),
          to.x + -radius * Math.cos(angle),
          to.y + radius * Math.sin(angle),
        ],
        fill: 'white',
        stroke: 'white',
        strokeWidth: 2,
      });

      this.layer.add(arrow);
      this.stage.draw();
    },
    connectShapes() {
      if (this.startShape && this.endShape && this.startShape !== this.endShape) {
        const startShapeId = this.startShape.id();
        const endShapeId = this.endShape.id();
        this.drawArrow();
        axios.post('http://localhost:5000/connect', {
            source: startShapeId,
            destination: endShapeId,
        }, {
            headers: {
                'Content-Type': 'application/json',
            },
        })
        .then((response) => {
            console.log(response);
            this.drawArrow();
        })
        .catch((error) => {
            console.error(error);
        });
      }
  },
  detect_queue(id) {
    // Find the circle with the given ID from backend
    const targetCircle = this.circles.find(circleObj => circleObj.id === id);

    if (targetCircle) {
      console.log("Required circle:", targetCircle.circle);
      targetCircle.text.text([
      'Q'+id,  // Replace 'New Line 1' with the desired text for line 1
      '2',  // Replace it with the number of products in queue from backend
       ].join('\n'));

    } else {
      console.log("Circle with ID", id, "not found.");
    }
  },
  detect_machine(id) {
    // Find the circle with the specified ID from backend
    const targetmachine = this.squares.find(squareObj => squareObj.id === id);
    if (targetmachine) {
      console.log("Required machine:", targetmachine.square);
      targetmachine.square.fill('blue') //example of changing color
      this.stage.draw();
    } else {
      console.log("machine with ID", id, "not found.");
    }
  },


  },
  mounted() {
    this.stage = new Konva.Stage({
      container: 'konva-container',
      width: window.innerWidth,
      height: window.innerHeight,
    });

    this.layer = new Konva.Layer();
    this.stage.add(this.layer);
  },
};
</script>


<style scoped>
</style>
