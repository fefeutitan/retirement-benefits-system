package br.com.beneficiosonline.integration.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.beneficiosonline.domain.documento.Documento;
import br.com.beneficiosonline.integration.aws.AwsProperties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@ConditionalOnProperty(name = "app.aws.enabled", havingValue = "true")
public class S3DocumentStorage implements DocumentStorage {
    private final S3Client s3Client;
    private final AwsProperties properties;

    public S3DocumentStorage(S3Client s3Client, AwsProperties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    @Override
    public void store(String key, String contentType, byte[] content) {
        s3Client.putObject(PutObjectRequest.builder().bucket(properties.getS3Bucket()).key(key)
                .contentType(contentType).build(), RequestBody.fromBytes(content));
    }

    @Override
    public byte[] load(Documento documento) {
        return s3Client.getObjectAsBytes(GetObjectRequest.builder().bucket(properties.getS3Bucket())
                .key(documento.getS3Key()).build()).asByteArray();
    }
}
