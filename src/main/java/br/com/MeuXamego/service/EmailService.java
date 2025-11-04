package br.com.MeuXamego.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import br.com.MeuXamego.model.Usuario;
import jakarta.mail.internet.MimeMessage;

/**
 * Serviço responsável por ENVIAR e-mails.
 * Aqui a gente encapsula o JavaMailSender do Spring, pra ficar fácil
 * trocar o template de e-mail depois (HTML, layout bonitinho etc).
 */
@Service
public class EmailService {

    // JavaMailSender é a abstração do Spring Boot para SMTP
    private final JavaMailSender mailSender;

    /**
     * Construtor com injeção de dependência.
     * O Spring vai criar um JavaMailSender com base nas configs do application.properties
     * e injetar aqui automaticamente.
     */
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * enviaCodigoVerificacao:
     * - Envia um e-mail simples de texto puro contendo o código de verificação
     * - Esse código será digitado depois pelo usuário no /verify/confirm
     *
     * @param destino e-mail do usuário que está verificando a conta
     * @param codigo  código de 5 dígitos gerado pelo sistema
     */
   public void enviarCodigoVerificacao(String destino, String codigo) {

        // SimpleMailMessage = e-mail de texto simples (sem HTML)
        SimpleMailMessage msg = new SimpleMailMessage();

        // Define para quem vai o e-mail (usuário alvo)
        msg.setTo(destino);

        // Assunto que aparece na caixa de entrada
        msg.setSubject("Seu código de verificação - MeuXamego");

        // Corpo do e-mail
        // Pode personalizar depois com nome do usuário, etc

       
        msg.setText(
               "Olá, "  +   "!\n\n" +
         "Seu código de verificação é: " + codigo + "\n" +


                "Se você não pediu este código, ignore este e-mail."
        );

        // Finalmente dispara o e-mail via SMTP configurado no application.properties
        mailSender.send(msg);
    }

    public void sendSimpleMessage(String email, String assunto, String corpo) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(email);
    msg.setSubject(assunto);
    msg.setText(corpo);
    mailSender.send(msg);
}

}