public class Caneta{
  public String modelo;
  public String cor;
  private float ponta;
  protected int carga;
  private boolean tampada; 
  
  void status(){
    System.out.println("modelo: " + this.modelo);
    System.out.println("uma caneta " + this.cor);
    System.out.println("Esta tampada ? " + this.tampada);
    System.out.println("ponta: " + this.ponta);
    System.out.println("cor : " + this.cor);
  }
  public void tampar(){
    this.tampada = true;

   }
   
   public void destampar(){
    this.tampada = false;
   }

   public void rabiscar(){
     if(tampada == true){
    System.out.println("ERRO ! NÃO POSSO RABISCAR ");

    }else{
        System.out.println("Estou rabiscando: ");
    }
   }
}